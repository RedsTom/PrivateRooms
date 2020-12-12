package org.reddev.privateroomsreborn.api.commands


import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.utils.BotConfig

trait TCommand {

    abstract void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args)

    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor()
    }

}
