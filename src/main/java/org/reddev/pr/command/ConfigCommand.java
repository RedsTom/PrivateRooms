/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.command;

import com.google.common.collect.Lists;
import fr.il_totore.ucp.CommandContext;
import fr.il_totore.ucp.GeneralResult;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.reddev.pr.Main;
import org.reddev.pr.command.subs.*;
import org.reddev.pr.utils.i18n.I18n;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ConfigCommand implements BiFunction<MessageCreateEvent, CommandContext<MessageCreateEvent>, GeneralResult> {

    Map<String, ConfigSubCommandExecutor> subs = new HashMap<>();

    {
        subs.put("help", new SubCommandHelp());
        subs.put("name", new SubCommandName());
        subs.put("setowner", new SubCommandSetOwner());
        subs.put("delete", new SubCommandDelete());
        subs.put("private", new SubCommandPrivate());
        //TODO private, public, add, remove
    }

    @Override
    public GeneralResult apply(MessageCreateEvent event, CommandContext<MessageCreateEvent> commandContext) {

        assert event.getServer().isPresent();
        Server server = event.getServer().get();
        User user = event.getMessageAuthor().asUser().orElse(null);
        TextChannel textChannel = event.getMessage().getChannel();
        if (user == null) {
            return null;
        }
        ServerVoiceChannel voiceChannel = user.getConnectedVoiceChannel(server).orElse(null);
        if (voiceChannel == null) {
            textChannel.sendMessage(Main.getErrorEmbed(I18n.format(server.getId(), "command.config.error.not_in_channel"), server));
            return null;
        }
        if (voiceChannel.getOverwrittenPermissions().get(user) == null || !(voiceChannel.getOverwrittenPermissions().get(user).getAllowedBitmask() == Main.userPermission)) {
            textChannel.sendMessage(Main.getErrorEmbed(I18n.format(server.getId(), "command.config.error.not_in_my_channel"), server));
            return null;
        }

        String[] args = event.getMessageContent().split(" ");
        List<String> argList = Lists.newArrayList(args);
        argList.remove(0);
        args = argList.toArray(new String[0]);

        if (args.length == 0) {
            args = new String[1];
            args[0] = "help";
        }

        String subCommandName = args[0];

        if (args.length == 1) {
            args = new String[0];
        } else {
            argList.remove(0);
            args = argList.toArray(new String[0]);
        }
        String[] finalArgs = args;
        subs.forEach((key, value) -> {
            if (key.equalsIgnoreCase(subCommandName)) {
                value.execute(server, user, voiceChannel, textChannel, subs, finalArgs, event);
            }
        });

        return null;
    }
}
