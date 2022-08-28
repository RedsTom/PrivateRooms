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

package me.redstom.privaterooms.commands;

import lombok.RequiredArgsConstructor;
import me.redstom.privaterooms.entities.services.GuildService;
import me.redstom.privaterooms.util.command.ICommand;
import me.redstom.privaterooms.util.command.RegisterCommand;
import me.redstom.privaterooms.util.i18n.I18n;
import me.redstom.privaterooms.util.i18n.Translator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@RegisterCommand
@RequiredArgsConstructor
public class DeleteCommand implements ICommand {

    private final GuildService guildService;

    private final I18n i18n;

    @Override
    public CommandData command() {
        return Commands.slash("delete", "Delete the channel");
    }


    private MessageEmbed successEmbed(SlashCommandInteractionEvent event) {

        Translator translator = i18n.of(guildService.rawOf(event.getGuild()).locale());

        return new EmbedBuilder()
          .setAuthor(event.getUser().getName(),
            null,
            event.getUser().getAvatarUrl())
          .setTitle(translator.get("commands.delete.title")
            .with("room_name", event.getChannel().getName())
            .toString())
          .setDescription(translator.get("commands.delete.description")
            .with("create_channel_id", guildService.of(event.getGuild().getIdLong()).createChannelId())
            .toString())
          .setColor(0x00FF00)
          .setImage(null)
          .build();
    }
}
