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

package me.redstom.privaterooms.util.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface Command {

    /**
     * The command definition.
     *
     * @return The command definition
     */
    CommandData command();

    /**
     * Checks if the command is valid.
     *
     * @param event The event that triggered the command.
     * @return True if the command is valid, false otherwise.
     */
    default boolean check(SlashCommandInteractionEvent event) {
        return true;
    }

    /**
     * Auto completes the command.
     *
     * @param event The event that triggered the suggestion demanding.
     */
    default void complete(CommandAutoCompleteInteractionEvent event) {
    }

}
