package org.reddev.privateroomsreborn.commands.settings

import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.commands.gensubs.SubHelp
import org.reddev.privateroomsreborn.commands.settings.subs.SCurrent
import org.reddev.privateroomsreborn.commands.settings.subs.SLanguage
import org.reddev.privateroomsreborn.commands.settings.subs.SAddWVC
import org.reddev.privateroomsreborn.commands.utils.CommandManager
import org.reddev.privateroomsreborn.utils.BotConfig

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l

class CommandSettings implements
        TCommand {

    private Map<List<String>, TCommand> subCommands

    CommandSettings() {
        subCommands = new HashMap<>()
        subCommands.put(["help", "?"], new SubHelp(cmds: subCommands, cmdName: "settings"))
        subCommands.put(["language", "lang", "locale"], new SLanguage())
        subCommands.put(["current", "config"], new SCurrent())
        subCommands.put(["add-wvc", "+wvc", "+whitelisted-voice-channel", "add-whitelisted-voice-channel"], new SAddWVC())
    }

    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
        CommandManager.repartSub(subCommands, event, cmd, config, args)
    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor(description: l("cmd.settings.description", guild),
                usage: "<subcommand>",
                permissions: [PermissionType.MANAGE_SERVER])
    }
}
