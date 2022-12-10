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

package me.redstom.privaterooms.util.room;

import lombok.RequiredArgsConstructor;
import me.redstom.privaterooms.entities.entity.*;
import me.redstom.privaterooms.entities.entity.model.Model;
import me.redstom.privaterooms.entities.entity.model.ModelEntityType;
import me.redstom.privaterooms.entities.entity.model.ModelRole;
import me.redstom.privaterooms.entities.entity.model.ModelUser;
import me.redstom.privaterooms.entities.services.GuildService;
import me.redstom.privaterooms.entities.services.RoomService;
import me.redstom.privaterooms.entities.services.UserService;
import me.redstom.privaterooms.util.Colors;
import me.redstom.privaterooms.util.i18n.I18n;
import me.redstom.privaterooms.util.i18n.Translator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoomCommandUtils {

    private final GuildService guildService;
    private final UserService userService;
    private final RoomService roomService;
    private final I18n i18n;

    public boolean check(SlashCommandInteractionEvent event) {
        Guild guild = guildService.of(event.getGuild().getIdLong());
        Member member = event.getMember();

        Translator translator = i18n.of(guild.locale());

        if (!member.getVoiceState().inAudioChannel()) {
            event.deferReply(true).setEmbeds(new EmbedBuilder()
              .setTitle(translator.raw("commands.config.error.title"))
              .setDescription(translator.raw("commands.config.error.not-in-voice"))
              .setColor(Colors.RED)
              .build()
            ).queue();

            return false;
        }

        VoiceChannel channel = (VoiceChannel) member.getVoiceState().getChannel();

        if (channel.getParentCategory().getIdLong() != guild.categoryId()) {
            event.deferReply(true).setEmbeds(new EmbedBuilder()
              .setTitle(translator.raw("commands.config.error.title"))
              .setDescription(translator.raw("commands.config.error.not-a-room"))
              .setColor(Colors.RED)
              .build()
            ).queue();

            return false;
        }

        Optional<Room> room = roomService.of(channel.getIdLong());
        if (room.isEmpty()) {
            event.deferReply(true).setEmbeds(new EmbedBuilder()
              .setTitle(translator.raw("commands.config.error.title"))
              .setDescription(translator.raw("commands.config.error.not-a-room"))
              .setColor(Colors.RED)
              .build()
            ).queue();

            return false;
        }

        Model model = room.get().model();
        boolean user = model.users().stream()
          .filter(u -> u.type() == ModelEntityType.MODERATOR)
          .map(ModelUser::referringUser)
          .map(User::discordId)
          .anyMatch(id -> id == member.getIdLong());

        boolean role = model.roles().stream()
          .filter(r -> r.type() == ModelEntityType.MODERATOR)
          .map(ModelRole::referringRole)
          .map(Role::discordId)
          .anyMatch(id -> member.getRoles().stream()
            .map(ISnowflake::getIdLong)
            .anyMatch(id::equals)
          );

        if (!(user || role || member.hasPermission(Permission.MANAGE_CHANNEL))) {
            event.deferReply(true).setEmbeds(new EmbedBuilder()
              .setTitle(translator.raw("commands.config.error.title"))
              .setDescription(translator.raw("commands.config.error.no-permissions"))
              .setColor(Colors.RED)
              .build()
            ).queue();

            return false;
        }

        return true;
    }

    public record RoomCommandContext(
      Guild guild,
      User user,
      Member member,
      Room room,
      Translator translator
    ) {
    }

    public RoomCommandContext contextOf(SlashCommandInteractionEvent event) {
        Guild guild = guildService.of(event.getGuild().getIdLong());
        User user = userService.of(event.getUser().getIdLong());
        Member member = event.getMember();

        Translator translator = i18n.of(guild.locale());
        Room room = roomService.of(member.getVoiceState().getChannel().getIdLong()).get();

        return new RoomCommandContext(guild, user, member, room, translator);
    }
}
