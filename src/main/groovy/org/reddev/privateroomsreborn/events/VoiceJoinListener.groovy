package org.reddev.privateroomsreborn.events

import org.javacord.api.entity.channel.Channel
import org.javacord.api.entity.channel.ServerVoiceChannel
import org.javacord.api.entity.server.Server
import org.javacord.api.event.channel.server.voice.ServerVoiceChannelMemberJoinEvent
import org.javacord.api.listener.channel.server.voice.ServerVoiceChannelMemberJoinListener
import org.reddev.privateroomsreborn.utils.ServerConfig
import org.reddev.privateroomsreborn.utils.channels.PrivateChannel
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

class VoiceJoinListener implements ServerVoiceChannelMemberJoinListener {
    @Override
    void onServerVoiceChannelMemberJoin(ServerVoiceChannelMemberJoinEvent event) {
        Server server = event.server
        ServerConfig config = ConfigUtils.getServerConfig(server)

        if (event.channel.idAsString == config.createChannelId && event.channel.category.isPresent() && event.channel.category.get().idAsString == config.categoryId) {


            PrivateChannel channel = new PrivateChannel(
                    name: "🔓 ${event.user.getDisplayName(server)}'s channel",
                    userLimit: 99,
                    moderators: Arrays.asList(event.user.id),
                    serverId: server.id,
            )
            channel.create(event.api).thenAccept {
                ServerVoiceChannel voiceChannel = server.getVoiceChannelById(channel.channelId).get()
                event.user.move(voiceChannel)
            }
        }
    }
}
