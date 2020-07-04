package org.reddev.pr.event;

import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.channel.server.voice.ServerVoiceChannelMemberLeaveEvent;
import org.javacord.api.listener.channel.server.voice.ServerVoiceChannelMemberLeaveListener;
import org.reddev.pr.Main;

public class ServerVoiceChannelMemberLeaveEventListener implements ServerVoiceChannelMemberLeaveListener {

    @Override
    public void onServerVoiceChannelMemberLeave(ServerVoiceChannelMemberLeaveEvent event) {

        try {
            Server server = event.getServer();
            ServerVoiceChannel voiceChannel = event.getChannel();
            User user = event.getUser();
            ChannelCategory category = voiceChannel.getCategory().orElse(null);
            if (category == null) return;
            if (voiceChannel.getId() == (long) Main.getDatabaseManager().getData("servers", "createChannelId", "id=" + server.getId())) {
                return;
            }
            if (category.getId() == (long) Main.getDatabaseManager().getData("servers", "categoryId", "id=" + server.getId())) {
                if (voiceChannel.getConnectedUsers().size() == 0 || voiceChannel.getConnectedUsers().isEmpty()) {
                    voiceChannel.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
