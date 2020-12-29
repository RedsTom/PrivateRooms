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
import static org.reddev.privateroomsreborn.utils.general.StringUtils.j

class CHide implements
        TCommand {
    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
        PrivateChannel channel = PrivateChannel.getFromChannel(config,
                ConfigUtils.getServerConfig(event.server.get()),
                event.server.get(),
                event.messageAuthor.connectedVoiceChannel.get()).get()
        channel.hidden = true
        channel.private_ = false
        channel.update(event.api).thenAccept {
            event.channel.sendMessage(l("cmd.config.hide.success", event.server.get()))
        }
    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        new CommandDescriptor(description: l("cmd.config.hide.description", guild))
    }
}
