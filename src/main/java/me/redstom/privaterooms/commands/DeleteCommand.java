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
import me.redstom.privaterooms.entities.services.RoomService;
import me.redstom.privaterooms.entities.services.TemplateService;
import me.redstom.privaterooms.util.Colors;
import me.redstom.privaterooms.util.command.CommandExecutor;
import me.redstom.privaterooms.util.command.ICommand;
import me.redstom.privaterooms.util.command.RegisterCommand;
import me.redstom.privaterooms.util.room.RoomCommandUtils;
import me.redstom.privaterooms.util.room.RoomCommandUtils.RoomCommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@RegisterCommand
@RequiredArgsConstructor
public class DeleteCommand implements ICommand {

    private final TemplateService templateService;
    private final RoomService roomService;

    private final RoomCommandUtils roomUtils;

    @Override
    public CommandData command() {
        return Commands.slash("delete", "Delete the channel");
    }

    @Override
    public boolean check(SlashCommandInteractionEvent event) {
        return roomUtils.check(event);
    }

    @CommandExecutor("delete")
    public void delete(SlashCommandInteractionEvent event) {
        RoomCommandContext ctx = roomUtils.contextOf(event);

        templateService.save("previous", ctx.user(), ctx.room().model());
        roomService.delete(ctx.room());

        event.deferReply(true).setEmbeds(new EmbedBuilder()
          .setAuthor(event.getUser().getName(),
            null,
            event.getUser().getAvatarUrl())
          .setTitle(ctx.translator().get("commands.delete.title")
            .with("room_name", ctx.room().model().channelName())
            .toString())
          .setDescription(ctx.translator().get("commands.delete.description")
            .with("create_channel_id", ctx.guild().createChannelId())
            .toString())
          .setColor(Colors.GREEN)
          .setImage(null)
          .build()
        ).queue();
    }
}
