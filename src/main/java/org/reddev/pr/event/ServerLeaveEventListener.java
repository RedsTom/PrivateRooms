/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.event;

import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.server.ServerLeaveEvent;
import org.javacord.api.listener.server.ServerLeaveListener;
import org.reddev.pr.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ServerLeaveEventListener implements ServerLeaveListener {
    public static void deleteChannels(Server server) throws SQLException {

        PreparedStatement stmt = Main.getDatabaseManager().getConnection().prepareStatement("SELECT categoryId FROM servers WHERE id=?");
        stmt.setLong(1, server.getId());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            long categoryId = rs.getLong(1);

            Optional<ChannelCategory> channelCategory = server.getChannelCategoryById(categoryId);
            if (channelCategory.isPresent()) {
                for (ServerChannel channel : channelCategory.get().getChannels()) {
                    channel.delete();
                }
                channelCategory.get().delete();
            }
        }
        rs.close();
        stmt.close();

    }

    @Override
    public void onServerLeave(ServerLeaveEvent event) {

        try {

            PreparedStatement stmt = Main.getDatabaseManager().getConnection().prepareStatement("DELETE FROM servers WHERE id=?");
            stmt.setLong(1, event.getServer().getId());
            stmt.execute();
            stmt.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
