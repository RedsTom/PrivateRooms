package org.reddev.pr.event;

import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ChannelCategoryBuilder;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.ServerVoiceChannelBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.listener.server.ServerJoinListener;
import org.reddev.pr.Main;
import org.reddev.pr.utils.i18n.I18n;

import java.sql.PreparedStatement;
import java.util.concurrent.CompletableFuture;

public class ServerJoinEventListener implements ServerJoinListener {

    public static void createChannels(Server server, boolean isUpdate) throws Exception {

        CompletableFuture<ChannelCategory> privateVoiceCategory = new ChannelCategoryBuilder(server)
                .setName("Private Rooms")
                .create();


        ServerVoiceChannelBuilder channelBuilder = new ServerVoiceChannelBuilder(server)
                .setName("🔒 " + I18n.format(server.getId(), "text.create_channel") + " 🔒")
                .setCategory(privateVoiceCategory.get())
                .setUserlimit(1);

        if (isUpdate) channelBuilder.setName("🔒 " + I18n.format(server.getId(), "text.create_channel") + " 🔒");
        else channelBuilder.setName("🔒 " + I18n.format("en", "text.create_channel") + " 🔒");

        ServerVoiceChannel channel = channelBuilder.create().get();

        if (isUpdate) {
            PreparedStatement stmt = Main.getDatabaseManager().getConnection().prepareStatement("UPDATE servers SET createChannelId=?, categoryId=? WHERE id=?");
            stmt.setLong(3, server.getId());
            stmt.setLong(1, channel.getId());
            stmt.setLong(2, privateVoiceCategory.get().getId());
            stmt.execute();
            stmt.close();
        } else {
            PreparedStatement stmt = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO servers (id, createChannelId, categoryId, lang) VALUES (?, ?, ?, ?)");
            stmt.setLong(1, server.getId());
            stmt.setLong(2, channel.getId());
            stmt.setLong(3, privateVoiceCategory.get().getId());
            stmt.setString(4, "en");
            stmt.execute();
            stmt.close();
        }
    }

    @Override
    public void onServerJoin(ServerJoinEvent event) {

        try {

            Server server = event.getServer();
            createChannels(server, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
