/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.command;

import fr.il_totore.ucp.CommandContext;
import fr.il_totore.ucp.GeneralResult;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.reddev.pr.Main;
import org.reddev.pr.utils.i18n.I18n;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.BiFunction;

public class ServerInfoCommands implements BiFunction<MessageCreateEvent, CommandContext<MessageCreateEvent>, GeneralResult> {

    @Override
    public GeneralResult apply(MessageCreateEvent event, CommandContext<MessageCreateEvent> messageCreateEventCommandContext) {

        try {

            assert event.getServer().isPresent();
            Server server = event.getServer().get();

            EmbedBuilder message = new EmbedBuilder()
                    .setTitle(I18n.format(server.getId(), "command.serverinfo.embed.title"))
                    .setDescription(I18n.format(server.getId(), "command.serverinfo.embed.description"))
                    .setColor(new Color(0, 209, 255));

            PreparedStatement q = Main.getDatabaseManager().getConnection().prepareStatement("SELECT * FROM servers WHERE id=?");
            q.setLong(1, server.getId());
            ResultSet rs = q.executeQuery();

            boolean foundInfo = false;
            while (rs.next()) {
                foundInfo = true;
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    message.addField("** \\▷  " + rs.getMetaData().getColumnName(i) + "**", String.valueOf(rs.getObject(i)));
                }
            }

            if (!(foundInfo)) {
                message.
                        setDescription(":x: Error : No information on your server !")
                        .addField(":bulb: Tip :", "Execute ``%setup`` to setup your server ! ");
            }

            event.getChannel().sendMessage(message);
            q.close();
            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
