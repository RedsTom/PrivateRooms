package org.reddev.pr.command;

import fr.il_totore.ucp.CommandContext;
import fr.il_totore.ucp.GeneralResult;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.function.BiFunction;

public class CommandPing implements BiFunction<MessageCreateEvent, CommandContext<MessageCreateEvent>, GeneralResult> {

    @Override
    public GeneralResult apply(MessageCreateEvent event, CommandContext<MessageCreateEvent> command) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Pinging...")
                .setColor(Color.RED);
        event.getChannel().sendMessage(builder).thenAccept((msg) -> {
            long ping =
                    msg.getCreationTimestamp().toEpochMilli() - event.getMessage().getCreationTimestamp().toEpochMilli();
            builder.setTitle("Pong ! ").setDescription("Bot's ping : **" + ping + "ms**");
            msg.edit(builder);
        });
        return null;
    }
}
