package org.reddev.privateroomsreborn.commands.utils

import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.ICommand
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.channels.PrivateChannel
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

class ProvCommandInfo implements ICommand {

    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
        PrivateChannel channel = PrivateChannel.getFromChannel(config, ConfigUtils.getServerConfig(event.server.get()), event.server.get(), event.messageAuthor.connectedVoiceChannel.get()).get()
        event.channel.sendMessage(channel.toString())

    }
}
