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

package org.gravendev.privaterooms.commands;

import fluent.bundle.FluentBundle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.gravendev.privaterooms.commands.utils.CommandContainer;
import org.gravendev.privaterooms.commands.utils.CommandDeclaration;
import org.gravendev.privaterooms.commands.utils.CommandExecutor;
import org.gravendev.privaterooms.i18n.LanguageMap;
import org.gravendev.privaterooms.i18n.commands.CommandLanguageManager;
import org.gravendev.privaterooms.i18n.commands.TranslatableCommandData;
import org.gravendev.privaterooms.i18n.keys.TranslationKeys;
import org.gravendev.privaterooms.utils.Colors;

import java.util.Map;

@CommandContainer
@RequiredArgsConstructor
public class PingCommand {

    private final LanguageMap languageMap;

    @CommandDeclaration
    public CommandData ping(CommandLanguageManager clm) {
        return clm.adapt(new TranslatableCommandData(
                PingCommandKeys.PING_NAME.key(), PingCommandKeys.PING_DESCRIPTION.key()));
    }

    @CommandExecutor("ping")
    public void execute(SlashCommandInteractionEvent event) {
        FluentBundle bundle = languageMap.get(event.getUserLocale());

        long gateway = event.getJDA().getGatewayPing();
        long rest = event.getJDA().getRestPing().complete();

        MessageEmbed embed =
                new EmbedBuilder()
                        .setTitle(PingCommandKeys.PING_EMBED_TITLE.format(bundle))
                        .setDescription(
                                PingCommandKeys.PING_EMBED_DESCRIPTION.format(
                                        bundle,
                                        Map.of(
                                                "gateway", gateway,
                                                "rest", rest)))
                        .setColor(Colors.BLUE)
                        .build();

        event.replyEmbeds(embed).queue();
    }

    @RequiredArgsConstructor
    @Getter
    private enum PingCommandKeys implements TranslationKeys {
        PING_NAME("commands-ping-name"),
        PING_DESCRIPTION("commands-ping-description"),
        PING_EMBED_TITLE("commands-ping-embed-title"),
        PING_EMBED_DESCRIPTION("commands-ping-embed-description");

        private final String key;
    }
}
