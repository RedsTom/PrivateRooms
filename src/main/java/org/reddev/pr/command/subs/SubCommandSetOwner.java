/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.command.subs;

import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.reddev.pr.Main;
import org.reddev.pr.utils.i18n.I18n;

import java.util.Map;

public class SubCommandSetOwner implements ConfigSubCommandExecutor {

    @Override
    public void execute(Server server, User user, ServerVoiceChannel voiceChannel, TextChannel textChannel, Map<String, ConfigSubCommandExecutor> subs, String[] args, MessageCreateEvent event) {

        if (event.getMessage().getMentionedUsers().size() != 1) {
            textChannel.sendMessage(Main.getErrorEmbed(I18n.format(server.getId(), "command.config.setowner.error.no_mention"), server));
            return;
        }

        User mentionnedUser = event.getMessage().getMentionedUsers().get(0);

        if (!(voiceChannel.getConnectedUsers().contains(mentionnedUser))) {
            textChannel.sendMessage(Main.getErrorEmbed(I18n.format(server.getId(), "command.config.setowner.error.not_connected"), server));
            return;
        }

        voiceChannel.createUpdater()
                .removePermissionOverwrite(user)
                .addPermissionOverwrite(mentionnedUser, Permissions.fromBitmask(Main.userPermission))
                .update();

        textChannel.sendMessage(Main.getSuccessEmbed(I18n.format(server.getId(), "command.config.setowner.successful.title"), String.format(I18n.format(server.getId(), "command.config.setowner.successful.description"), mentionnedUser.getMentionTag())));

    }

    @Override
    public String getUsage() {
        return "[mention]";
    }

    @Override
    public String getDescription(Server server) {
        return I18n.format(server.getId(), "command.config.setowner.description");
    }
}
