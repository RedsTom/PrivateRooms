package org.reddev.privateroomsreborn.events

import org.javacord.api.event.channel.server.voice.ServerVoiceChannelMemberLeaveEvent
import org.javacord.api.listener.channel.server.voice.ServerVoiceChannelMemberLeaveListener
import org.reddev.privateroomsreborn.utils.ServerConfig
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

class VoiceLeaveListener implements ServerVoiceChannelMemberLeaveListener {
    @Override
    void onServerVoiceChannelMemberLeave(ServerVoiceChannelMemberLeaveEvent event) {
        ServerConfig config = ConfigUtils.getServerConfig(event.server)
        event.channel.category.ifPresent {
            if (event.channel.connectedUsers.size() == 0) {
                if (event.channel.category.get().idAsString == config.categoryId
                        && !config.whitelistedVoiceChannels.contains(event.channel.id)
                        && event.channel.idAsString != config.createChannelId) {
                    event.channel.delete("No longer users in channel")
                }
            }

        }
    }
}
