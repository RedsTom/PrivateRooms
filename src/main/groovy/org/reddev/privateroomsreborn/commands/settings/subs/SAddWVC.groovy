package org.reddev.privateroomsreborn.commands.settings.subs

import org.javacord.api.entity.channel.ServerVoiceChannel
import org.javacord.api.entity.channel.VoiceChannel
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.ServerConfig
import org.reddev.privateroomsreborn.utils.general.CommandUtils
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l
import static org.reddev.privateroomsreborn.utils.general.StringUtils.j

class SAddWVC implements TCommand {
    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
        // TODO Test
        ServerConfig sConfig = ConfigUtils.getServerConfig(event.server.get())
        if (!args[0] || args.length < 1) {
            CommandUtils.sendBadUsage(event, cmd, this)
            return
        }
        if (!args[0].isLong()) {
            CommandUtils.sendBadUsage(event, cmd, this)
            return
        }
        Optional<ServerVoiceChannel> oChannel = event.server.get().getVoiceChannelById(args[0])
        if (!oChannel.isPresent()) {
            CommandUtils.sendBadUsage(event, cmd, this)
            return
        }
        sConfig.whitelistedVoiceChannels.add(args[0].toLong())
        ConfigUtils.update(event.server.get(), sConfig)
        event.channel.sendMessage(j(l("cmd.settings.wvc.success", event.server.get()), oChannel.get().name))
    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor(usage: "<channel id>", description: l("cmd.settings.wvc.description", guild))
    }
}
