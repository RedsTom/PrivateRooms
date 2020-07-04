/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.command;

import fr.il_totore.ucp.CommandContext;
import fr.il_totore.ucp.GeneralResult;
import org.javacord.api.event.message.MessageCreateEvent;
import org.reddev.pr.EmbedUtils;
import org.reddev.pr.event.ServerLeaveEventListener;
import org.reddev.pr.utils.i18n.I18n;

import java.util.function.BiFunction;

public class ClearDataCommand implements BiFunction<MessageCreateEvent, CommandContext<MessageCreateEvent>, GeneralResult> {
    @Override
    public GeneralResult apply(MessageCreateEvent event, CommandContext<MessageCreateEvent> messageCreateEventCommandContext) {

        try {
            ServerLeaveEventListener.deleteChannels(event.getServer().get());
            event.getChannel().sendMessage(EmbedUtils.getSuccessEmbed(I18n.format(event.getServer().get().getId(), "command.cleardata.successful.title"), I18n.format(event.getServer().get().getId(), "command.cleardata.successful.description")));
        } catch (Exception e) {
            e.printStackTrace();
            event.getChannel().sendMessage(EmbedUtils.getErrorEmbed(I18n.format(event.getServer().get().getId(), "error.sql_error"), event.getServer().get()));
        }

        return null;
    }
}
