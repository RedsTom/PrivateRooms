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
import org.reddev.pr.EmbedUtils;
import org.reddev.pr.utils.i18n.I18n;

import java.util.Map;

public class SubCommandName implements ConfigSubCommandExecutor {

    @Override
    public void execute(Server server, User user, ServerVoiceChannel voiceChannel, TextChannel textChannel, Map<String, ConfigSubCommandExecutor> subs, String[] args, MessageCreateEvent event) {

        if (args.length < 1) {
            textChannel.sendMessage(EmbedUtils.getErrorEmbed(I18n.format(server.getId(), "command.config.name.error.syntax"), server));
            return;
        }
        voiceChannel.createUpdater().setName("🔐 " + String.join(" ", args)).update();
        textChannel.sendMessage(EmbedUtils.getSuccessEmbed(I18n.format(server.getId(), "command.config.name.successful.title"), String.format(I18n.format(server.getId(), "command.config.name.successful.description"), String.join(" ", args))));

    }

    @Override
    public String getDescription(Server server) {
        return I18n.format(server.getId(), "command.config.name.description");
    }

    @Override
    public String getUsage() {
        return "[new name]";
    }
}
