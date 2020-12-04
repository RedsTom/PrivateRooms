package org.reddev.privateroomsreborn.commands.config

import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.commands.gensubs.SSubHelp
import org.reddev.privateroomsreborn.commands.settings.subs.SSubLanguage
import org.reddev.privateroomsreborn.commands.utils.CommandManager
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.ICommand

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l

class CommandConfig implements ICommand {

    private Map<List<String>, ICommand> subCommands

    CommandConfig() {
        subCommands = new HashMap<>()
        subCommands.put(["help", "?"], new SSubHelp(cmds: subCommands, cmdName: "config"))
    }

    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {

        CommandManager.repartSub(subCommands, event, cmd, config, args)

    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor(description: l("cmd.config.cmd-desc", guild), usage: "<subcommand>")
    }

}
