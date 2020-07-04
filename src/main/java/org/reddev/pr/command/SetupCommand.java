/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.command;

import fr.il_totore.ucp.CommandContext;
import fr.il_totore.ucp.GeneralResult;
import org.javacord.api.event.message.MessageCreateEvent;
import org.reddev.pr.Main;
import org.reddev.pr.event.ServerJoinEventListener;
import org.reddev.pr.event.ServerLeaveEventListener;
import org.reddev.pr.utils.i18n.I18n;

import java.util.function.BiFunction;

public class SetupCommand implements BiFunction<MessageCreateEvent, CommandContext<MessageCreateEvent>, GeneralResult> {
    @Override
    public GeneralResult apply(MessageCreateEvent event, CommandContext<MessageCreateEvent> messageCreateEventCommandContext) {

        try {
            ServerLeaveEventListener.deleteChannels(event.getServer().get());
            ServerJoinEventListener.createChannels(event.getServer().get(), true);
            event.getChannel().sendMessage(Main.getSuccessEmbed(I18n.format(event.getServer().get().getId(), "command.setup.successful.title"), I18n.format(event.getServer().get().getId(), "command.setup.successful.description")));
        } catch (Exception e) {
            e.printStackTrace();
            event.getChannel().sendMessage(Main.getErrorEmbed(e.getMessage(), event.getServer().get()));
        }

        return null;
    }
}
