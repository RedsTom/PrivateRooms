/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.reddev.pr.utils.i18n.I18n;

import java.awt.*;

public class EmbedUtils {

    public static EmbedBuilder getErrorEmbed(String s, Server server) {
        return new EmbedBuilder()
                .setTitle(I18n.format(server.getId(), "message.error") + " : ")
                .setDescription(s)
                .setColor(Color.RED);
    }

    public static EmbedBuilder getSuccessEmbed(String title, String message) {
        return new EmbedBuilder()
                .setTitle(title)
                .setDescription(message)
                .setColor(Color.GREEN);
    }

}
