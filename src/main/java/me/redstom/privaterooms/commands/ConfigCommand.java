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
import me.redstom.privaterooms.entities.entity.User;
import me.redstom.privaterooms.entities.services.UserService;
import me.redstom.privaterooms.util.command.ICommand;
import me.redstom.privaterooms.util.command.RegisterCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.function.Function;

@RegisterCommand
@RequiredArgsConstructor
public class ConfigCommand implements ICommand {

    private final UserService userService;

    @Override
    public CommandData command() {
        Function<String, SubcommandGroupData> listGroup = (name) ->
          new SubcommandGroupData(name, "Manage the %s of the channel".formatted(name))
            .addSubcommands(
              new SubcommandData("add", "Add a user to the %s".formatted(name))
                .addOption(OptionType.USER, "user", "The user to add to the %s".formatted(name), true),
              new SubcommandData("remove", "Removes an user from the %s".formatted(name))
                .addOption(OptionType.USER, "user", "The user to remove from the %s".formatted(name), true),
              new SubcommandData("add-role", "Adds a role to the %s".formatted(name))
                .addOption(OptionType.ROLE, "role", "The role to add to the %s".formatted(name), true),
              new SubcommandData("remove-role", "Removes a role from the %s".formatted(name))
                .addOption(OptionType.ROLE, "role", "The role to remove from the %s".formatted(name), true),
              new SubcommandData("list", "List the users in the %s".formatted(name))
                .addOptions(new OptionData(OptionType.STRING, "filter", "The filter of the %s".formatted(name), false)
                  .addChoice("all", "0")
                  .addChoice("user", "1")
                  .addChoice("role", "2")
                )
            );

        return Commands.slash("config", "Configure your current channel")
          .setGuildOnly(true)
          .addSubcommands(
            new SubcommandData("name", "Rename the channel")
              .addOption(OptionType.STRING, "name", "The new name of the channel", true),
            new SubcommandData("visibility", "Changes the visibility of the channel")
              .addOptions(new OptionData(OptionType.STRING, "visibility", "The new visibility of the channel", true, false)
                .addChoice("public", "0")
                .addChoice("private", "1")
                .addChoice("hidden", "2")
              )
          ).addSubcommandGroups(
            listGroup.apply("whitelist"),
            listGroup.apply("blacklist"),
            listGroup.apply("moderators")
          );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        User user = userService.of(event.getUser().getIdLong());
    }
}
