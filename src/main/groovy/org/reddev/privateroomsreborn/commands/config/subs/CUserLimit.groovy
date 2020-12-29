package org.reddev.privateroomsreborn.commands.config.subs

import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.channels.PrivateChannel
import org.reddev.privateroomsreborn.utils.general.CommandUtils
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l

class CUserLimit implements
        TCommand {
    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
        if (args.length != 1) {
            CommandUtils.sendBadUsage(event, cmd, this)
            return
        }
        try {
            args[0].toInteger()
        } catch (NumberFormatException ignored) {
            event.channel.sendMessage(l("cmd.config.user-limit.not-valid-number", event.server.get()))
            return
        }
        int userLimit = args[0].toInteger()
        PrivateChannel channel = PrivateChannel.getFromChannel(config,
                ConfigUtils.getServerConfig(event.server.get()),
                event.server.get(),
                event.messageAuthor.connectedVoiceChannel.get()).get()
        channel.userLimit = userLimit
        channel.update(event.api).thenAccept {
            event.channel.sendMessage(l("cmd.config.user-limit.success", event.server.get()))
        }
    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        new CommandDescriptor(usage: "<user limit>")
    }
}
