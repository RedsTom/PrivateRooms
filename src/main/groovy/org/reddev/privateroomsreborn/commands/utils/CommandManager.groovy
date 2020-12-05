package org.reddev.privateroomsreborn.commands.utils

import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.commands.CommandPing
import org.reddev.privateroomsreborn.commands.CommandSetup
import org.reddev.privateroomsreborn.commands.DefaultCommand

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
        // TODO Système de preset :
        /*
         * %preset save : Enregistre le preset dans (presets/{random uuid}.json) et donne l'uuid à l'utilisateur
         * %preset load <uuid> : Charge le preset dans le salon actuel
         */
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
            dispatchCommand(commands, cmd, config, event, prefix + cmd, args)
        }

    }

    static void repartSub(Map<List<String>, ICommand> subs, MessageCreateEvent event, String originCmd, BotConfig config, String[] args) {
        if (args.length <= 0) {
            args = ["help"]
        }
        String cmd = args[0]
        args = Arrays.copyOfRange(args, 1, args.length)
        dispatchCommand(subs, cmd, config, event, originCmd, args)
    }

    private static void dispatchCommand(Map<List<String>, ICommand> commands, String input, BotConfig config, MessageCreateEvent event, String originCmd, String[] args) {
        String cmd
        ICommand iCmd = new DefaultCommand()
        commands.forEach { names, ex ->
            if (names.contains(input))
                iCmd = ex
        }
        if (CommandUtils.hasPermission(config,
                event.messageAuthor.asUser().get(),
                event.server.get(),
                iCmd.getDescriptor(event.server.get()).permissions)) {
            iCmd.execute(event, config, j("%s %s", originCmd, input), args)
        } else {
            event.channel.sendMessage(l("errors.no-permission", event.server.get()))
        }
    }
}
