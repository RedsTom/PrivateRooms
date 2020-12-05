package org.reddev.privateroomsreborn.commands

import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.ICommand
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.channels.PrivateChannel
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

class ProvCommandInfo implements ICommand {
    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
        event.channel.sendMessage(
                PrivateChannel.getFromChannel(
                        ConfigUtils.getServerConfig(
                                event.server.get()),
                        event.server.get(),
                        event.messageAuthor.getConnectedVoiceChannel().get()
                ).get().toString()
        )
    }
}
