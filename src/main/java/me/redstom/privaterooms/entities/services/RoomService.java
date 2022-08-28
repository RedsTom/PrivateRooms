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
import me.redstom.privaterooms.entities.entity.Model;
import me.redstom.privaterooms.entities.entity.Room;
import me.redstom.privaterooms.entities.repository.RoomRepository;
import me.redstom.privaterooms.util.i18n.I18n;
import me.redstom.privaterooms.util.i18n.Translator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.Manager;
import net.dv8tion.jda.api.managers.channel.concrete.VoiceChannelManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final JDA client;
    private final RoomRepository roomRepository;
    private final GuildService guildService;
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

        Room room = of(roomRepository.save(Room.builder()
          .model(Model.builder()
            .channelName(name)
            .build())
          .discordId(channel.getIdLong())
          .guild(g)
          .build()
        ));

        g.discordGuild().moveVoiceMember(member, channel).complete();
        log.info("{} ({}) created a room : \"{}\" on \"{}\" ({})",
          member.getEffectiveName(),
          member.getId(),
          name,
          g.discordGuild().getName(),
          g.discordId()
        );

        return update(room, r -> r);
    }

    public Room update(Room r, UnaryOperator<Room> updated) {
        Room room = roomRepository.save(updated.apply(r));
        if (room.discordChannel() == null) {
            room = of(r);
        }

        VoiceChannel voiceChannel = room.discordChannel();

        String emoji = switch (room.model().visibility()) {
            case PUBLIC -> "🔓";
            case PRIVATE -> "🔐";
            case HIDDEN -> "🔒";
        };

        Optional<VoiceChannelManager> manager = Optional.of(voiceChannel.getManager()
          .setName("%s %s".formatted(emoji, room.model().channelName()))
          .setUserLimit(room.model().maxUsers())
        );

        room.model().whitelistUsers().forEach(u -> {
            Member member = voiceChannel.getGuild().getMember(u.discordUser());
            if (member == null) return;

            manager.map(m -> m.putPermissionOverride(member, List.of(
              Permission.VIEW_CHANNEL,
              Permission.VOICE_CONNECT,
              Permission.VOICE_SPEAK,
              Permission.VOICE_START_ACTIVITIES,
              Permission.VOICE_STREAM,
              Permission.VOICE_USE_VAD
            ), List.of()));
        });

        room.model().blacklistUsers().forEach(u -> {
            Member member = voiceChannel.getGuild().getMember(u.discordUser());
            if (member == null) return;

            manager.map(m -> m.putPermissionOverride(member,
              0,
              Permission.VIEW_CHANNEL.getRawValue() | Permission.ALL_VOICE_PERMISSIONS)
            );
        });

        room.model().moderatorUsers().forEach(u -> {
            Member member = voiceChannel.getGuild().getMember(u.discordUser());
            if (member == null) return;

            manager.map(m -> m.putPermissionOverride(member, Permission.ALL_VOICE_PERMISSIONS, 0));
        });

        room.model().whitelistRoles().forEach(role -> manager.map(m ->
          m.putPermissionOverride(role.discordRole(), List.of(
            Permission.VIEW_CHANNEL,
            Permission.VOICE_CONNECT,
            Permission.VOICE_SPEAK,
            Permission.VOICE_START_ACTIVITIES,
            Permission.VOICE_STREAM,
            Permission.VOICE_USE_VAD
          ), List.of())
        ));

        room.model().blacklistRoles().forEach(role -> manager.map(m ->
          m.putPermissionOverride(role.discordRole(),
            0,
            Permission.VIEW_CHANNEL.getRawValue() | Permission.ALL_VOICE_PERMISSIONS)
        ));

        room.model().moderatorRoles().forEach(role -> manager.map(m ->
          m.putPermissionOverride(role.discordRole(), Permission.ALL_VOICE_PERMISSIONS, 0)
        ));

        manager.ifPresent(Manager::queue);

        return room;
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
}
