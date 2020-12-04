package org.reddev.privateroomsreborn.commands.utils

import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.commands.CommandPing
import org.reddev.privateroomsreborn.commands.CommandSetup
import org.reddev.privateroomsreborn.commands.DefaultCommand
import org.reddev.privateroomsreborn.commands.ProvCommandInfo
import org.reddev.privateroomsreborn.commands.config.CommandConfig
import org.reddev.privateroomsreborn.commands.gensubs.SSubHelp
import org.reddev.privateroomsreborn.commands.settings.CommandSettings
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.api.commands.ICommand
import org.reddev.privateroomsreborn.utils.general.CommandUtils

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l
import static org.reddev.privateroomsreborn.utils.general.StringUtils.j

class CommandManager {

    private static final Map<List<String>, ICommand> commands = new HashMap<>();

    static {
        commands.put(["ping", "p?"], new CommandPing())
        commands.put(["settings", "parameters"], new CommandSettings())
        commands.put(["config", "c"], new CommandConfig())
        commands.put(["help", "?"], new SSubHelp(cmds: commands))
        commands.put(["setup"], new CommandSetup())
        commands.put(["info"], new ProvCommandInfo())
    }

    static void onMessage(MessageCreateEvent event, BotConfig config) {

        if (event.message.author.botUser)
            return
        if (event.message.groupMessage) {
            event.channel.sendMessage(l("errors.no-dm", "en"))
            return
        }

        if (CommandUtils.matchPrefix(config, event.messageContent, event.server.get())) {
            String[] args = event.message.content
                    .split(" ")
            String prefix = CommandUtils.getPrefix(config, event.server.get())
            String cmd = args[0].substring(prefix.length())
            args = Arrays.copyOfRange(args, 1, args.length)
            ICommand command = new DefaultCommand()
            commands.forEach { aliases, executor ->
                if (aliases.contains(cmd))
                    command = executor
            }
            if (CommandUtils.hasPermission(config,
                    event.messageAuthor.asUser().get(),
                    event.server.get(),
                    command.getDescriptor(event.server.get()).permissions)) {
                command.execute(event, config, prefix + cmd, args)
            } else {
                event.channel.sendMessage(l("errors.no-permission", event.server.get()))
            }
        }

    }

    static void repartSub(Map<List<String>, ICommand> subs, MessageCreateEvent event, String originCmd, BotConfig config, String[] args) {
        if (args.length <= 0) {
            args = ["help"]
        }
        String cmd = args[0]
        args = Arrays.copyOfRange(args, 1, args.length)
        ICommand iCmd = new DefaultCommand()
        subs.forEach { names, ex ->
            if (names.contains(cmd))
                iCmd = ex
        }
        iCmd.execute(event, config, j("%s %s", originCmd, cmd), args)
    }
}
