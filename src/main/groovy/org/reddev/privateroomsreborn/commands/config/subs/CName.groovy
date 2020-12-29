package org.reddev.privateroomsreborn.commands.config.subs

import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.channels.PrivateChannel
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l

class CName implements TCommand {

    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
        PrivateChannel channel = PrivateChannel.getFromChannel(
                config,
                ConfigUtils.getServerConfig(event.server.get()),
                event.server.get(),
                event.messageAuthor.connectedVoiceChannel.get()
        ).get()
        channel.name = "🔒 ${args.join(" ")}"
        channel.update(event.api).thenAcceptAsync {
            event.channel.sendMessage("Nom changé !")
        }

    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor(
                permissions: [],
                description: l("cmd.config.name.desc", guild),
                usage: "<new name>"
        )
    }
}
