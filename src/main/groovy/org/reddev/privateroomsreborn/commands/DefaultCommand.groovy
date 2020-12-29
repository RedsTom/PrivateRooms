package org.reddev.privateroomsreborn.commands

import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.utils.BotConfig

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l

class DefaultCommand implements TCommand {

    @Override
    void execute(MessageCreateEvent ev, BotConfig cfg, String cmd, String[] args) {
        ev.channel.sendMessage(l("errors.no-exists", ev.server.get()))
    }
}
