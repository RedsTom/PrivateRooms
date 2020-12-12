package org.reddev.privateroomsreborn.events

import org.javacord.api.event.channel.server.voice.ServerVoiceChannelMemberLeaveEvent
import org.javacord.api.listener.channel.server.voice.ServerVoiceChannelMemberLeaveListener
import org.reddev.privateroomsreborn.utils.ServerConfig
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

class VoiceLeaveListener implements ServerVoiceChannelMemberLeaveListener {
    @Override
    void onServerVoiceChannelMemberLeave(ServerVoiceChannelMemberLeaveEvent event) {
        ServerConfig config = ConfigUtils.getServerConfig(event.server)
        println(config.whitelistedVoiceChannels)
        println(event.channel.idAsString)
        if (event.channel.category.isPresent()) {
            if (event.channel.connectedUsers.size() == 0) {
                if (event.channel.category.get().idAsString == config.categoryId
                        && !config.whitelistedVoiceChannels.contains(event.channel.category.get().id)
                        && event.channel.idAsString != config.createChannelId) {
                    event.channel.delete("No longer users in channel")
                }
            }
        }
    }
}
