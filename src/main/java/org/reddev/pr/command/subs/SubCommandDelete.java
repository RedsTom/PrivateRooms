/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.command.subs;

import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.reddev.pr.Main;
import org.reddev.pr.utils.i18n.I18n;

import java.util.Map;

public class SubCommandDelete implements ConfigSubCommandExecutor {

    @Override
    public void execute(Server server, User user, ServerVoiceChannel voiceChannel, TextChannel textChannel, Map<String, ConfigSubCommandExecutor> subs, String[] args, MessageCreateEvent event) {

        voiceChannel.delete("Deleted cause it was asked ;)");
        textChannel.sendMessage(Main.getSuccessEmbed(I18n.format(server.getId(), "command.config.delete.successful.title"), I18n.format(server.getId(), "command.config.delete.successful.description")));

    }

}
