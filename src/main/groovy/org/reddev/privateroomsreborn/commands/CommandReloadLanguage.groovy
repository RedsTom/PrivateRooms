package org.reddev.privateroomsreborn.commands


import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.general.CommandUtils
import org.reddev.privateroomsreborn.utils.general.LangUtils

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l

class CommandReloadLanguage implements TCommand {
    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {

        if (!CommandUtils.isAdmin(config, event.messageAuthor.asUser().get())) {
            event.channel.sendMessage(l("errors.no-permission", event.server.get()))
            return
        }

        LangUtils.updateLanguageCache(config)

    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor(description: l("cmd.reload-language.description", guild))
    }
}
