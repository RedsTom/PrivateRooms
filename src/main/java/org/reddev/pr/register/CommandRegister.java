/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.register;

import fr.il_totore.ucp.CommandSpec;
import fr.il_totore.ucp.registration.CommandRegistry;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.event.message.MessageCreateEvent;
import org.reddev.pr.command.*;
import org.reddev.pr.utils.spec.PermissionedCommandSpec;

public class CommandRegister {
    public static void register(CommandRegistry<MessageCreateEvent> registry) {

        registry.register(new PermissionedCommandSpec<MessageCreateEvent>("server-info").permissionized(Permissions.fromBitmask(8)).executing(new ServerInfoCommands()).describedAs("command.serverinfo.description"));
        registry.register(new PermissionedCommandSpec<MessageCreateEvent>("setup").permissionized(Permissions.fromBitmask(8)).executing(new SetupCommand()).describedAs("command.setup.description"));
        registry.register(new PermissionedCommandSpec<MessageCreateEvent>("clear-data").permissionized(Permissions.fromBitmask(8)).executing(new ClearDataCommand()).describedAs("command.cleardata.description"));
        registry.register(new PermissionedCommandSpec<MessageCreateEvent>("lang").permissionized(Permissions.fromBitmask(8)).executing(new LangCommand()).describedAs("command.lang.description"));
        registry.register(new CommandSpec.ImplicitSpec<MessageCreateEvent>("config").executing(new ConfigCommand()).describedAs("command.config.description"));
        registry.register(new CommandSpec.ImplicitSpec<MessageCreateEvent>("help").executing(new HelpCommand()).describedAs("command.help.description"));

    }
}
