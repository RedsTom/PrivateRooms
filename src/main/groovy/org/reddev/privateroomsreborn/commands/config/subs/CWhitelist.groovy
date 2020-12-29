package org.reddev.privateroomsreborn.commands.config.subs

import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.commands.config.subs.whitelist.RoleWhitelistCommands
import org.reddev.privateroomsreborn.commands.config.subs.whitelist.UserWhitelistCommands
import org.reddev.privateroomsreborn.commands.gensubs.SubHelp
import org.reddev.privateroomsreborn.commands.utils.CommandManager
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.channels.PrivateChannel
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

import java.awt.Color

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l
import static org.reddev.privateroomsreborn.utils.general.StringUtils.j

class CWhitelist implements TCommand {

    private Map<List<String>, TCommand> subCommands

    CWhitelist() {
        subCommands = new HashMap<>()
        subCommands.put(["help", "?"], new SubHelp(cmds: subCommands, cmdName: "config whitelist"))
        subCommands.put(["add", "+", "add-user", "+u"], new UserWhitelistCommands.WLUserAdd())
        subCommands.put(["remove", "-", "remove-user", "-u"], new UserWhitelistCommands.WLUserRemove())
        subCommands.put(["show", "=", "show-users", "=u", "show-roles", "=r"], new WLShow())
        subCommands.put(["add-role", "+r", "add-r"], new RoleWhitelistCommands.WLRoleAdd())
        subCommands.put(["remove-role", "-r", "remove-r"], new RoleWhitelistCommands.WLRoleRemove())
    }

    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
        CommandManager.repartSub(subCommands, event, cmd, config, args)
    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor()
    }

    static class WLShow implements TCommand {

        @Override
        void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {

            PrivateChannel channel = PrivateChannel.getFromChannel(
                    config,
                    ConfigUtils.getServerConfig(event.server.get()),
                    event.server.get(),
                    event.messageAuthor.connectedVoiceChannel.get()
            ).get()

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(l("cmd.config.whitelist.show.title", event.server.get()))
                    .setColor(Color.GREEN)

            List<String> users = new ArrayList<>()
            channel.whitelistedUsers.forEach { users.add(event.api.getUserById(it).get().discriminatedName) }
            embedBuilder.addField("Utilisateurs :", users.size() > 0 ? "`" + users.join("`, `") + "`" : l("cmd.config.whitelist.show.no-value-present", event.server.get()))

            List<String> roles = new ArrayList<>()
            channel.whitelistedRoles.forEach { roles.add(event.server.get().getRoleById(it).get().name) }
            embedBuilder.addField("Roles :", roles.size() > 0 ? "`" + roles.join("`, `") + "`" : l("cmd.config.whitelist.show.no-value-present", event.server.get()))

            event.channel.sendMessage(embedBuilder)
        }

        @Override
        CommandDescriptor getDescriptor(Server guild) {
            return new CommandDescriptor()
        }
    }
}
