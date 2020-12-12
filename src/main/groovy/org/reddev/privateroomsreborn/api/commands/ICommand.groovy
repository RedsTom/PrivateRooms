package org.reddev.privateroomsreborn.api.commands

import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.utils.BotConfig

@FunctionalInterface
interface ICommand {

    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args)

    default CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor()
    }

}
