/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.command.subs;

import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.reddev.pr.utils.i18n.I18n;

import java.awt.*;
import java.util.Map;

public class SubCommandHelp implements ConfigSubCommandExecutor {

    @Override
    public void execute(Server server, User user, ServerVoiceChannel voiceChannel, TextChannel textChannel, Map<String, ConfigSubCommandExecutor> subs, String[] args, MessageCreateEvent event) {

        EmbedBuilder embed = new EmbedBuilder().setTitle(I18n.format(server.getId(), "text.help")).setDescription("").setColor(new Color(0xFAFAFA));

        subs.forEach((key, value) -> embed.addField(" **\\▷ %config " + key + "**" + (!value.getUsage().isEmpty() ? " *" + value.getUsage() + "*" : ""), "```css\n" + value.getDescription(server) + "```", true));

        textChannel.sendMessage(embed);

    }

    @Override
    public String getDescription(Server server) {
        return I18n.format(server.getId(), "command.config.help.description");
    }
}
