/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.event;

import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.ServerVoiceChannelBuilder;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.channel.server.voice.ServerVoiceChannelMemberJoinEvent;
import org.javacord.api.listener.channel.server.voice.ServerVoiceChannelMemberJoinListener;
import org.reddev.pr.Main;

import java.util.Optional;

public class ServerVoiceChannelMemberJoinEventListener implements ServerVoiceChannelMemberJoinListener {
    @Override
    public void onServerVoiceChannelMemberJoin(ServerVoiceChannelMemberJoinEvent event) {

        try {
            Server server = event.getServer();
            ServerVoiceChannel voiceChannel = event.getChannel();
            User user = event.getUser();

            if ((Long) Main.getDatabaseManager().getData("servers", "createChannelId", "id=" + server.getId()) == voiceChannel.getId()) {

                Optional<ChannelCategory> oCategory = server.getChannelCategoryById((Long) Main.getDatabaseManager().getData("servers", "categoryId", "id=" + server.getId()));
                ChannelCategory category = oCategory.orElse(null);

                ServerVoiceChannelBuilder builder = new ServerVoiceChannelBuilder(server)
                        .setName("🔐 " + user.getName() + "'s channel")
                        .addPermissionOverwrite(user, Permissions.fromBitmask(Main.ownerPermission));

                if (category != null) builder.setCategory(category);

                ServerVoiceChannel channelCreated = builder.create().get();
                user.move(channelCreated);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
