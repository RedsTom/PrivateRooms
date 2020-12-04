package org.reddev.privateroomsreborn.commands


import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.ICommand

import java.awt.*

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l
import static org.reddev.privateroomsreborn.utils.general.StringUtils.j

class CommandPing implements ICommand {
    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
        event.channel.sendMessage(new EmbedBuilder()
                .setTitle("Pinging...")
                .setColor(Color.RED))
                .thenAccept { msg ->
                    long ping = msg.creationTimestamp.toEpochMilli() - event.message.creationTimestamp.toEpochMilli()
                    msg.edit(new EmbedBuilder()
                            .setTitle("Pong !")
                            .setDescription(j("**%s :** *${ping}ms*", l("cmd.ping.bot-ping", event.server.get())))
                            .setColor(Color.GREEN))
                }
    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor(description: l("cmd.ping.desc", guild))
    }
}
