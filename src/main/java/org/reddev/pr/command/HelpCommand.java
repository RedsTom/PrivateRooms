/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.command;

import fr.il_totore.ucp.CommandContext;
import fr.il_totore.ucp.GeneralResult;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.reddev.pr.Main;
import org.reddev.pr.utils.i18n.I18n;

import java.awt.*;
import java.util.function.BiFunction;

public class HelpCommand implements BiFunction<MessageCreateEvent, CommandContext<MessageCreateEvent>, GeneralResult> {
    @Override
    public GeneralResult apply(MessageCreateEvent event, CommandContext<MessageCreateEvent> commandContext) {

        EmbedBuilder embed = new EmbedBuilder().setTitle(I18n.format(event.getServer().get().getId(), "text.help")).setDescription("").setColor(new Color(0xFAFAFA));

        Main.getRegistry().getRegisteredCommands().forEach((command) ->
                embed.addField(" **\\▷ %" + command.getName() + "**", "```css\n" + (command.getDescription().isPresent() ? I18n.format(event.getServer().get().getId(), command.getDescription().get()) : I18n.format(event.getServer().get().getId(), "text.no_description")) + "```", true)
        );

        event.getChannel().sendMessage(embed);

        return null;
    }
}
