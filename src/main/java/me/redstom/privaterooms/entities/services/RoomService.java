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
import me.redstom.privaterooms.entities.entity.Guild;
import me.redstom.privaterooms.entities.entity.Room;
import me.redstom.privaterooms.entities.entity.User;
import me.redstom.privaterooms.entities.entity.model.*;
import me.redstom.privaterooms.entities.repository.ModelRoleRepository;
import me.redstom.privaterooms.entities.repository.ModelUserRepository;
import me.redstom.privaterooms.entities.repository.RoomRepository;
import me.redstom.privaterooms.util.ActionMapper;
import me.redstom.privaterooms.util.i18n.I18n;
import me.redstom.privaterooms.util.i18n.Translator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.channel.concrete.VoiceChannelManager;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
        VoiceChannel channel = category
          .createVoiceChannel("🔓 %s".formatted(name))
          .setUserlimit(99)
          .addPermissionOverride(member, Permission.ALL_VOICE_PERMISSIONS, 0)
          .reason("User %s#%s (%s) created a room".formatted(
            member.getEffectiveName(),
            member.getUser().getDiscriminator(),
            member.getIdLong()
          ))
          .complete();

        Room room = of(save(Room.builder()
          .model(Model.builder()
            .channelName(name)
            .user(ModelUser.builder()
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

        return room;
    }

    private Room save(Room room) {
        modelRoleRepository.saveAll(room.model().roles());
        modelUserRepository.saveAll(room.model().users());

        return roomRepository.save(room);
    }

    public CompletableFuture<Room> update(Room room, Member issuer, UnaryOperator<ModelSynchronizerService> update) {
        if (issuer != null) {
            User user = userService.of(issuer.getIdLong());
            templateService.save("previous", user, room.model());
        }

        ModelSynchronizerService modelSynchronizerService = new ModelSynchronizerService(room);
        update.apply(modelSynchronizerService);
        modelSynchronizerService
          .reason("%s edited the channel configuration".formatted(issuer == null ? "System" : issuer.getEffectiveName()));

        return modelSynchronizerService.queue().thenApply(finalRoom -> {
            log.info("\"{}\" issued a modification to room \"{}\" on \"{}\" ({}), did : {}",
              issuer == null ? "System" : issuer.getEffectiveName(),
              finalRoom.model().channelName(),
              finalRoom.guild().discordGuild().getName(),
              finalRoom.guild().discordId(),
              modelSynchronizerService.actionsIsEmpty()
                ? "NOTHING"
                : "\n - " + String.join("\n - ", modelSynchronizerService.actions())
            );
            return finalRoom;
        });
    }

    public Room update(@Nullable Member issuer, Room r, UnaryOperator<Model> updated) {

        Room old = Room.copyOf(r);
        Room room = save(r.model(updated.apply(r.model())));

        if (room.discordChannel() == null) {
            room = of(r);
        }

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

        ActionMapper<VoiceChannelManager> manager = ActionMapper.of(voiceChannel.getManager()
          .setName("%s %s".formatted(emoji, room.model().channelName()))
        );

        final Model model = room.model();
        if (old.model().userLimit() != room.model().userLimit()) {
            manager.map(
              m -> m.setUserLimit(model.userLimit()),
              "Set user limit to %s".formatted(model.userLimit())
            );
        }

        if (old.model().visibility() != room.model().visibility()) {
            switch (room.model().visibility()) {
                case PRIVATE -> manager.map(
                  m -> m.putPermissionOverride(
                    voiceChannel.getGuild().getPublicRole(),
                    0,
                    Permission.VOICE_CONNECT.getRawValue()
                  ),
                  "Set channel visibility to PRIVATE"
                );
                case HIDDEN -> manager.map(m -> m.putPermissionOverride(
                    voiceChannel.getGuild().getPublicRole(),
                    0,
                    Permission.getRaw(
                      Permission.VOICE_CONNECT,
                      Permission.VIEW_CHANNEL
                    )
                  ),
                  "Set channel visibility to HIDDEN"
                );
                case PUBLIC -> manager.map(m -> m.removePermissionOverride(
                    voiceChannel.getGuild().getPublicRole()
                  ),
                  "Set channel visibility to PUBLIC"
                );
            }
        }

        for (ModelUser u : room.model().users()) {
            if (old.model().users().stream()
              .filter(u1 -> Objects.equals(u.referringUser().id(), u1.referringUser().id()))
              .noneMatch(u1 -> u.type() == u1.type())) {
                Member member = voiceChannel.getGuild().retrieveMemberById(userService.of(u.referringUser()).discordId())
                  .complete();
                if (member == null) {
                    continue;
                }

                setPermissions(manager, u, member);
            }
        }

        room.model().roles().stream()
          .filter(r0 -> old.model().roles().stream()
            .filter(r1 -> Objects.equals(r0.referringRole().id(), r1.referringRole().id()))
            .noneMatch(r1 -> r0.type() == r1.type()))
          .forEach(modelRole -> {
              net.dv8tion.jda.api.entities.Role role = roleService.of(modelRole.referringRole()).discordRole();

              setPermissions(manager, modelRole, role);
          });

        Room finalRoom = room;
        manager.then((m, actions) -> m
          .reason("%s edited the channel configuration".formatted(issuer == null ? "System" : issuer.getEffectiveName()))
          .queue(__ -> {
              log.info("\"{}\" issued a modification to room \"{}\" on \"{}\" ({}), did : {}",
                issuer == null ? "System" : issuer.getEffectiveName(),
                finalRoom.model().channelName(),
                finalRoom.guild().discordGuild().getName(),
                finalRoom.guild().discordId(),
                actions.length == 0 ? "NOTHING" : "\n - " + String.join("\n - ", actions)
              );
          })
        );

        return room;
    }

    private static void setPermissions(ActionMapper<VoiceChannelManager> manager, ModelEntity e, IPermissionHolder permissible) {
        System.out.println("Type : " + e.type());
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
              ),
              "Added %s to the whitelist".formatted(permissible.getId())
            );
            case BLACKLIST -> manager.map(m -> m.putPermissionOverride(
                permissible,
                0,
                Permission.VIEW_CHANNEL.getRawValue() | Permission.ALL_VOICE_PERMISSIONS
              ),
              "Added %s to the blacklist".formatted(permissible.getId())
            );
            case MODERATOR -> manager.map(m -> m.putPermissionOverride(
                permissible,
                Permission.ALL_VOICE_PERMISSIONS,
                0
              ),
              "Added %s to the moderators".formatted(permissible.getId())
            );
        }
    }

    public Optional<Room> rawOf(VoiceChannel guild) {
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

    public Room of(Room room) {
        if (room.discordChannel() != null) {
            return room;
        }
        Guild guild = guildService.of(room.guild());
        return room.discordChannel(guild.discordGuild().getVoiceChannelById(room.discordId()));
    }

    public void delete(Room room) {
        room.discordChannel().delete()
          .reason("Room deleted")
          .queue();
        roomRepository.delete(room);
    }
}
