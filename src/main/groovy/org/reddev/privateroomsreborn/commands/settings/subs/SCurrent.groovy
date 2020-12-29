package org.reddev.privateroomsreborn.commands.settings.subs

import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.ServerConfig
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

import java.awt.*

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l

class SCurrent implements TCommand {

    @Override
    void execute(MessageCreateEvent event, BotConfig bConfig, String cmd, String[] args) {

        ServerConfig config = ConfigUtils.getServerConfig(event.server.get())
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(l("cmd.settings.embed.title", event.server.get()))
                .setColor(Color.YELLOW)
                .addField("Custom Prefix", "```" + config.getCustomPrefix(bConfig) + "```", true)
                .addField("Language", "```" + config.language + "```", true)
                .addField("Create Channel Id", "```" + config.createChannelId + "```", true)
                .addField("Category Id", "```" + config.categoryId + "```", true)
                .addField("Disabled", "```" + config.deleted.toString() + "```", true)

        event.channel.sendMessage(builder)

    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor(description: l("cmd.settings.current.description", guild))
    }
}
