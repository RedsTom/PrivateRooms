package org.reddev.privateroomsreborn.commands.utils

import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.commands.CommandPing
import org.reddev.privateroomsreborn.commands.CommandReloadLanguage
import org.reddev.privateroomsreborn.commands.CommandSetup
import org.reddev.privateroomsreborn.commands.DefaultCommand
import org.reddev.privateroomsreborn.commands.config.CommandConfig
import org.reddev.privateroomsreborn.commands.gensubs.SubHelp
import org.reddev.privateroomsreborn.commands.preset.CommandPreset
import org.reddev.privateroomsreborn.commands.settings.CommandSettings
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.general.CommandUtils

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l
import static org.reddev.privateroomsreborn.utils.general.StringUtils.j

class CommandManager {

    private static final Map<List<String>, TCommand> commands = new HashMap<>()

    static {
        commands.put(["ping", "p?"], new CommandPing())
        commands.put(["settings", "parameters"], new CommandSettings())
        commands.put(["config", "c", "configure", "c!", "cfg"], new CommandConfig())
        commands.put(["help", "?"], new SubHelp(cmds: commands))
        commands.put(["setup", "s!"], new CommandSetup())
        commands.put(["preset", "p", "template", "t"], new CommandPreset())
        commands.put(["reload-language"], new CommandReloadLanguage())
    }

    static void onMessage(MessageCreateEvent event, BotConfig config) {

        if (event.message.author.botUser) {
            return
        }
        if (event.message.groupMessage) {
            event.channel.sendMessage(l("errors.no-dm", "en"))
            return
        }
        if (event.message.mentionedUsers.contains(event.api.yourself)) {
            event.channel.sendMessage(j("Prefix for this server : `%s`", CommandUtils.getPrefix(config, event.server.get())))
            return
        }

        if (CommandUtils.matchPrefix(config, event.messageContent, event.server.get())) {
            String[] args = event.message.content
                    .split(" ")
            String prefix = CommandUtils.getPrefix(config, event.server.get())
            String cmd = args[0].substring(prefix.length())
            args = Arrays.copyOfRange(args, 1, args.length)
            dispatchCommand(commands, cmd, config, event, prefix + cmd, args, true)
        }

    }

    static void repartSub(Map<List<String>, TCommand> subs, MessageCreateEvent event, String originCmd, BotConfig config, String[] args) {
        if (args.length <= 0) {
            args = ["help"]
        }
        String cmd = args[0]
        args = Arrays.copyOfRange(args, 1, args.length)

        dispatchCommand(subs, cmd, config, event, originCmd, args)
    }

    private static void dispatchCommand(Map<List<String>, TCommand> commands, String input, BotConfig config, MessageCreateEvent event, String originCmd, String[] args, boolean original = false) {
        TCommand iCmd = new DefaultCommand()
        commands.forEach { names, ex ->
            if (names.contains(input))
                iCmd = ex
        }
        if (CommandUtils.hasPermission(config,
                event.messageAuthor.asUser().get(),
                event.server.get(),
                iCmd.getDescriptor(event.server.get()).permissions)) {
            iCmd.execute(event, config, "${originCmd}${original ? "" : " ${input}"}", args)
        } else {
            event.channel.sendMessage(l("errors.no-permission", event.server.get()))
        }
    }
}
