package org.reddev.privateroomsreborn.commands.gensubs

import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.ICommand
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.ServerConfig
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

import java.awt.Color

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l
import static org.reddev.privateroomsreborn.utils.general.StringUtils.j

class SSubHelp implements ICommand {

    Map<List<String>, ICommand> cmds
    String cmdName = ""

    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {

        ServerConfig cfg = ConfigUtils.getServerConfig(event.server.get())

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(j("%s", l("cmd.settings.help.title", event.server.get())))
                .setDescription(j("%s `%s`", l("cmd.settings.help.title", event.server.get()), cfg.getCustomPrefix(config) + cmdName))
                .setColor(Color.GREEN)

        if (cmdName) cmdName += " "
        cmds.forEach { name, executor ->
            builder.addField(cfg.getCustomPrefix(config) + cmdName + name[0], "```css\n${executor.getDescriptor(event.server.get()).description}```")
        }
        if (cmdName) cmdName -= " "

        event.channel.sendMessage(builder)

    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor(description: l("cmd.subs.help.cmd-desc", guild))
    }
}
