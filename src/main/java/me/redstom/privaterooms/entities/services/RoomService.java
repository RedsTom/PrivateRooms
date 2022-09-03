/*
 * PrivateRooms is a discord bot to manage vocal chats.
 * Copyright (C) 2022 GravenDev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.redstom.privaterooms.entities.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.redstom.privaterooms.entities.entity.*;
import me.redstom.privaterooms.entities.repository.ModelRoleRepository;
import me.redstom.privaterooms.entities.repository.ModelUserRepository;
import me.redstom.privaterooms.entities.repository.RoomRepository;
import me.redstom.privaterooms.util.i18n.I18n;
import me.redstom.privaterooms.util.i18n.Translator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.channel.concrete.VoiceChannelManager;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final JDA client;
    private final RoomRepository roomRepository;
    private final ModelUserRepository modelUserRepository;
    private final ModelRoleRepository modelRoleRepository;
    private final GuildService guildService;
    private final RoleService roleService;
    private final UserService userService;
    private final TemplateService templateService;
    private final I18n i18n;

    public Room create(Guild g, Member member) {
        if (g.discordGuild() == null) {
            g = guildService.of(g);
        }

        Translator translator = i18n.of(g.locale());

        String name = translator.get("channels.default-name")
          .with("name", member.getEffectiveName())
          .toString();

        Category category = g.discordGuild().getCategoryById(g.categoryId());
        VoiceChannel channel = category.createVoiceChannel(name)
          .complete();

        Room room = of(save(Room.builder()
          .model(Model.builder()
            .channelName(name)
            .user(ModelEntity.ModelUser.builder()
              .referringUser(userService.of(member.getIdLong()))
              .type(ModelEntityType.MODERATOR)
              .build()
            )
            .build())
          .discordId(channel.getIdLong())
          .guild(g)
          .build()
        ));

        g.discordGuild().moveVoiceMember(member, channel).complete();
        log.info("\"{}\" ({}) created a room : \"{}\" on \"{}\" ({})",
          member.getEffectiveName(),
          member.getId(),
          name,
          g.discordGuild().getName(),
          g.discordId()
        );

        return update(null, room, r -> r);
    }

    private Room save(Room room) {
        modelRoleRepository.saveAll(room.model().roles());
        modelUserRepository.saveAll(room.model().users());

        return roomRepository.save(room);
    }

    public Room update(@Nullable Member issuer, Room r, UnaryOperator<Model> updated) {
        Room old = new Room(r);
        Room room = save(r.model(updated.apply(r.model())));
        if (room.discordChannel() == null) {
            room = of(r);
        }

        log.info("\"{}\" issued a modification to room \"{}\" on \"{}\" ({})",
          issuer == null ? "System" : issuer.getEffectiveName(),
          room.model().channelName(),
          room.guild().discordGuild().getName(),
          room.guild().discordId()
        );

        if (issuer != null) {
            User user = userService.of(issuer.getIdLong());
            templateService.save("previous", user, room.model());
        }

        VoiceChannel voiceChannel = room.discordChannel();

        String emoji = switch (room.model().visibility()) {
            case PUBLIC -> "🔓";
            case PRIVATE -> "🔐";
            case HIDDEN -> "🔒";
        };

        Optional<VoiceChannelManager> manager = Optional.of(voiceChannel.getManager()
          .setName("%s %s".formatted(emoji, room.model().channelName()))
        );

        final Model model = room.model();
        if (old.model().maxUsers() != room.model().maxUsers()) {
            manager.map(m -> m.setUserLimit(model.maxUsers()));
        }

        if (old.model().visibility() != room.model().visibility()) {
            switch (room.model().visibility()) {
                case PRIVATE -> manager.map(m -> m.putPermissionOverride(
                  voiceChannel.getGuild().getPublicRole(),
                  0,
                  Permission.VOICE_CONNECT.getRawValue()
                ));
                case HIDDEN -> manager.map(m -> m.putPermissionOverride(
                  voiceChannel.getGuild().getPublicRole(),
                  0,
                  Permission.getRaw(
                    Permission.VOICE_CONNECT,
                    Permission.VIEW_CHANNEL
                  )
                ));
            }
        }

        System.out.println(old.model().users().size());
        System.out.println(room.model().users().size());

        if (old.model().users().size() != room.model().users().size()) {
            room.model().users().forEach(u -> {
                Member member = voiceChannel.getGuild().getMember(userService.of(u.referringUser()).discordUser());
                if (member == null) return;

                setPermissions(manager, u, member);
            });
        }

        if (old.model().roles().size() != room.model().roles().size()) {
            room.model().roles().forEach(modelRole -> {
                net.dv8tion.jda.api.entities.Role role = roleService.of(modelRole.referringRole()).discordRole();

                setPermissions(manager, modelRole, role);
            });
        }

        manager.ifPresent(m -> m
          .reason("%s edited the channel configuration".formatted(issuer == null ? "System" : issuer.getEffectiveName()))
          .queue()
        );

        return room;
    }

    private static void setPermissions(Optional<VoiceChannelManager> manager, ModelEntity e, IPermissionHolder permissible) {
        switch (e.type()) {
            case WHITELIST -> manager.map(m -> m.putPermissionOverride(
              permissible,
              List.of(
                Permission.VIEW_CHANNEL,
                Permission.VOICE_CONNECT,
                Permission.VOICE_SPEAK,
                Permission.VOICE_START_ACTIVITIES,
                Permission.VOICE_STREAM,
                Permission.VOICE_USE_VAD
              ),
              List.of()
            ));
            case BLACKLIST -> manager.map(m -> m.putPermissionOverride(
              permissible,
              0,
              Permission.VIEW_CHANNEL.getRawValue() | Permission.ALL_VOICE_PERMISSIONS
            ));
            case MODERATOR -> manager.map(m -> m.putPermissionOverride(
              permissible,
              Permission.ALL_VOICE_PERMISSIONS,
              0
            ));
        }
    }

    public Optional<Room> rawOf(net.dv8tion.jda.api.entities.VoiceChannel guild) {
        return rawOf(guild.getIdLong());
    }

    public Optional<Room> rawOf(long discordId) {
        return roomRepository
          .findByDiscordId(discordId);
    }

    public Optional<Room> of(long discordId) {
        return rawOf(discordId)
          .map(this::of);
    }

    public Room of(Room r) {
        Guild guild = guildService.of(r.guild());
        return r.discordChannel(guild.discordGuild().getVoiceChannelById(r.discordId()));
    }

    public void delete(Room room) {
        room.discordChannel().delete()
          .reason("Room deleted")
          .queue();
        roomRepository.delete(room);
    }
}
