package org.reddev.privateroomsreborn.commands.config.subs.blacklist

import org.javacord.api.entity.permission.Role
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.channels.PrivateChannel
import org.reddev.privateroomsreborn.utils.general.ConfigUtils
import org.reddev.privateroomsreborn.utils.general.MatchRoleQuery

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l
import static org.reddev.privateroomsreborn.utils.general.StringUtils.j

class RoleBlacklistCommands {

    static class BLRoleAdd implements TCommand {

        @Override
        void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
            PrivateChannel channel = PrivateChannel.getFromChannel(
                    config,
                    ConfigUtils.getServerConfig(event.server.get()),
                    event.server.get(),
                    event.messageAuthor.connectedVoiceChannel.get()
            ).get()

            List<Role> roles = new ArrayList<>()
            int matchFound = 0
            String roleQuery = args.join(" ")
            for (Role role in event.server.get().roles) {
                if (role.name.containsIgnoreCase(roleQuery)) {
                    matchFound++
                    roles.add(role)
                }
            }

            new MatchRoleQuery(event.channel, roles).then { role ->
                channel.blacklistedRoles.add(role.id)
                channel.update(event.api).thenAccept {
                    event.channel.sendMessage(j(l("cmd.config.blacklist.add.role.success", event.server.get()), role.name))
                }
            }.start()
        }

        @Override
        CommandDescriptor getDescriptor(Server guild) {
            return new CommandDescriptor(description: l("cmd.config.blacklist.add.role.description", guild), usage: "<name>")
        }
    }

    static class BLRoleRemove implements TCommand {

        @Override
        void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
            PrivateChannel channel = PrivateChannel.getFromChannel(
                    config,
                    ConfigUtils.getServerConfig(event.server.get()),
                    event.server.get(),
                    event.messageAuthor.connectedVoiceChannel.get()
            ).get()

            List<Role> roles = new ArrayList<>()
            int matchFound = 0
            String roleQuery = args.join(" ")
            for (Role role in event.server.get().roles) {
                if (role.name.containsIgnoreCase(roleQuery)) {
                    matchFound++
                    roles.add(role)
                }
            }

            new MatchRoleQuery(event.channel, roles).then { role ->
                channel.blacklistedRoles.remove(role.id)
                channel.update(event.api).thenAccept {
                    event.channel.sendMessage(j(l("cmd.config.blacklist.remove.role.success", event.server.get()), role.name))
                    channel.clear(event.messageAuthor.connectedVoiceChannel.get(), role)
                }
            }.start()
        }

        @Override
        CommandDescriptor getDescriptor(Server guild) {
            return new CommandDescriptor(description: l("cmd.config.blacklist.remove.role.description", guild), usage: "<name>")
        }
    }

}
