/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.command.configsubs;

import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.reddev.pr.EmbedUtils;
import org.reddev.pr.command.configsubs.utils.ConfigSubCommandExecutor;
import org.reddev.pr.utils.i18n.I18n;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class SubCommandRemove implements ConfigSubCommandExecutor {
    @Override
    public void execute(Server server, User user, ServerVoiceChannel voiceChannel, TextChannel textChannel,
                        Map<String, ConfigSubCommandExecutor> subs, String[] args, MessageCreateEvent event) {


        AtomicReference<User> mentionnedUser = new AtomicReference<>();
        if (event.getMessage().getMentionedUsers().size() != 1) {
            if (args.length > 0) {
                try {
                    long potentialId = Long.parseLong(args[0]);
                    server.getMemberById(potentialId).ifPresent(mentionnedUser::set);
                } catch (NumberFormatException e) {
                }
            }
            if (mentionnedUser.get() == null) {
                textChannel.sendMessage(EmbedUtils.getErrorEmbed(I18n.format(server.getId(), "command.config.remove" +
                                                                                             ".error.no_mention"),
                        server));
                return;
            }
        } else {
            mentionnedUser.set(event.getMessage().getMentionedUsers().get(0));
        }

        voiceChannel.createUpdater().removePermissionOverwrite(mentionnedUser.get()).update();
        textChannel.sendMessage(EmbedUtils.getSuccessEmbed(I18n.format(server.getId(), "command.config.remove" +
                                                                                       ".successful.title"),
                String.format(I18n.format(server.getId(), "command.config.remove.successful.description"),
                        mentionnedUser.get().getMentionTag())));

    }

    @Override
    public String getDescription(Server server) {
        return I18n.format(server.getId(), "command.config.remove.description");
    }

    @Override
    public String getUsage() {
        return "<@mention>";
    }
}
