package org.reddev.privateroomsreborn.commands.gensubs

import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.general.CommandUtils

import java.awt.*
import java.util.List

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l
import static org.reddev.privateroomsreborn.utils.general.StringUtils.j

class SubHelp implements TCommand {

    Map<List<String>, TCommand> cmds
    String cmdName = ""

    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {

        try {
            List<String> cmdSplit = cmd.split(" ").toList()
            cmdSplit.removeLast()
            def prefix = cmdSplit.size() == 0 ? CommandUtils.getPrefix(config, event.server.get()) : ""

            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle(j("%s", l("cmd.help.title", event.server.get())))
                    .setDescription(cmdSplit.size() == 0 ? "" : j("%s `%s`", l("cmd.help.desc", event.server.get()), cmdSplit.join(" ")))
                    .setColor(Color.GREEN)

            cmds.forEach { name, executor ->
                def usage = executor.getDescriptor(event.server.get()).getUsage()
                builder.addField("${cmdSplit.join(" ")} ${prefix}${name[0]} ${usage.isEmpty() ? "" : "`${usage}`"}", """
                | ${executor.getDescriptor(event.server.get()).description}
                |
                | > **${l("cmd.help.aliases", event.server.get())}**
                | `${name.join("`, `")}`
                | ~~------------------------------~~
                """.stripMargin(), true)
            }
            if (cmdName) cmdName -= " "

            event.channel.sendMessage(builder)

        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor(description: l("cmd.subs.help.cmd-desc", guild))
    }
}
