package org.reddev.privateroomsreborn.commands.config.subs

import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.ICommand
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.channels.PrivateChannel
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

class SSubName implements ICommand {

    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
        PrivateChannel channel = PrivateChannel.getFromChannel(
                ConfigUtils.getServerConfig(event.server.get()),
                event.server.get(),
                event.messageAuthor.connectedVoiceChannel.get()
        ).get()
        channel.name = "🔒 ${args.join(" ")}"
        channel.update(event.api, {
            event.channel.sendMessage("Nom changé !")
        })

    }
}
