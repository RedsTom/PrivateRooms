/*
 * PrivateRooms is a bot managing vocal channels in a server
 * Copyright (C) 2022 RedsTom
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

package org.gravendev.privaterooms.listeners;

import fluent.bundle.FluentBundle;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.gravendev.privaterooms.commands.utils.CommandExecutorRepr;
import org.gravendev.privaterooms.i18n.LanguageMap;
import org.gravendev.privaterooms.i18n.keys.ErrorKeys;
import org.gravendev.privaterooms.listeners.utils.Listener;
import org.gravendev.privaterooms.utils.Colors;

import java.util.Map;

@Listener
@RequiredArgsConstructor
public class CommandListener extends ListenerAdapter {

    private final Map<String, CommandExecutorRepr> commands;
    private final LanguageMap bundles;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        CommandExecutorRepr command = commands.get(event.getCommandId());
        if (command != null) {
            try {
                command.run(event);
            } catch (Exception e) {
                FluentBundle bundle = bundles.get(event.getUserLocale());

                MessageEmbed errorEmbed = new EmbedBuilder()
                        .setTitle(ErrorKeys.ERROR_TITLE.format(bundle))
                        .setTitle(ErrorKeys.ERROR_DESCRIPTION.format(bundle, Map.of("message", e.getMessage())))
                        .setColor(Colors.RED)
                        .build();

                event.replyEmbeds(errorEmbed)
                        .setEphemeral(true)
                        .queue();
            }
        }
    }
}
