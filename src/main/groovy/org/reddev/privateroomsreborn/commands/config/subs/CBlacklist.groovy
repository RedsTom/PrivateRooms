package org.reddev.privateroomsreborn.commands.config.subs

import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.commands.config.subs.blacklist.RoleBlacklistCommands
import org.reddev.privateroomsreborn.commands.config.subs.blacklist.UserBlacklistCommands
import org.reddev.privateroomsreborn.commands.config.subs.whitelist.RoleWhitelistCommands
import org.reddev.privateroomsreborn.commands.config.subs.whitelist.UserWhitelistCommands
import org.reddev.privateroomsreborn.commands.gensubs.SubHelp
import org.reddev.privateroomsreborn.commands.utils.CommandManager
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.channels.PrivateChannel
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

import java.awt.*
import java.util.List

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l

class CBlacklist implements TCommand {

    private Map<List<String>, TCommand> subCommands

    CBlacklist() {
        subCommands = new HashMap<>()
        subCommands.put(["help", "?"], new SubHelp(cmds: subCommands, cmdName: "config whitelist"))
        subCommands.put(["add", "+", "add-user", "+u"], new UserBlacklistCommands.BLUserAdd())
        subCommands.put(["remove", "-", "remove-user", "-u"], new UserBlacklistCommands.BLUserRemove())
        subCommands.put(["show", "=", "show-users", "=u", "show-roles", "=r"], new BLShow())
        subCommands.put(["add-role", "+r", "add-r"], new RoleBlacklistCommands.BLRoleAdd())
        subCommands.put(["remove-role", "-r", "remove-r"], new RoleBlacklistCommands.BLRoleRemove())
    }

    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
        CommandManager.repartSub(subCommands, event, cmd, config, args)
    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor(description: l("cmd.config.blacklist.description", guild))
    }

    static class BLShow implements TCommand {

        @Override
        void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {

            PrivateChannel channel = PrivateChannel.getFromChannel(
                    config,
                    ConfigUtils.getServerConfig(event.server.get()),
                    event.server.get(),
                    event.messageAuthor.connectedVoiceChannel.get()
            ).get()

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(l("cmd.config.blacklist.show.title", event.server.get()))
                    .setColor(Color.GREEN)

            List<String> users = new ArrayList<>()
            channel.blacklistedUsers.forEach { users.add(event.api.getUserById(it).get().discriminatedName) }
            embedBuilder.addField("Utilisateurs :", users.size() > 0 ? "`" + users.join("`, `") + "`" : l("cmd.config.blacklist.show.no-value-present", event.server.get()))

            List<String> roles = new ArrayList<>()
            channel.blacklistedRoles.forEach { roles.add(event.server.get().getRoleById(it).get().name) }
            embedBuilder.addField("Roles :", roles.size() > 0 ? "`" + roles.join("`, `") + "`" : l("cmd.config.blacklist.show.no-value-present", event.server.get()))

            event.channel.sendMessage(embedBuilder)
        }

        @Override
        CommandDescriptor getDescriptor(Server guild) {
            return new CommandDescriptor(description: l("cmd.config.blacklist.show.description", guild))
        }
    }
}
