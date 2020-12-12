package org.reddev.privateroomsreborn.commands.settings

import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.commands.gensubs.SSubHelp
import org.reddev.privateroomsreborn.commands.settings.subs.SSubCurrent
import org.reddev.privateroomsreborn.commands.settings.subs.SSubLanguage
import org.reddev.privateroomsreborn.commands.utils.CommandManager
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.TCommand

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l

class CommandSettings implements
        TCommand {

    private Map<List<String>, TCommand> subCommands

    CommandSettings() {
        subCommands = new HashMap<>()
        subCommands.put(["language", "lang", "locale"], new SSubLanguage())
        subCommands.put(["current", "config"], new SSubCurrent())
        subCommands.put(["help", "?"], new SSubHelp(cmds: subCommands, cmdName: "settings"))
    }

    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
        CommandManager.repartSub(subCommands, event, cmd, config, args)
    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor(description: l("cmd.settings.cmd-desc", guild),
                usage: "<subcommand>",
                permissions: [PermissionType.MANAGE_SERVER])
    }
}
