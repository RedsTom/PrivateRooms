package org.reddev.privateroomsreborn.commands.config

import org.apache.groovy.groovysh.Command
import org.javacord.api.entity.channel.ServerVoiceChannel
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.commands.config.subs.CBlacklist
import org.reddev.privateroomsreborn.commands.config.subs.CHide
import org.reddev.privateroomsreborn.commands.config.subs.CModeration
import org.reddev.privateroomsreborn.commands.config.subs.CName
import org.reddev.privateroomsreborn.commands.config.subs.CPrivate
import org.reddev.privateroomsreborn.commands.config.subs.CPublic
import org.reddev.privateroomsreborn.commands.config.subs.CUserLimit
import org.reddev.privateroomsreborn.commands.config.subs.CWhitelist
import org.reddev.privateroomsreborn.commands.gensubs.SubHelp
import org.reddev.privateroomsreborn.commands.utils.CommandManager
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.ServerConfig
import org.reddev.privateroomsreborn.utils.channels.PrivateChannel
import org.reddev.privateroomsreborn.utils.general.CommandUtils
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l

class CommandConfig implements TCommand {

    private Map<List<String>, TCommand> subCommands

    CommandConfig() {
        subCommands = new HashMap<>()
        subCommands.put(["help", "?"], new SubHelp(cmds: subCommands, cmdName: "config"))
        subCommands.put(["name", "rename"], new CName())
        subCommands.put(["userlimit", "user-limit", "limit"], new CUserLimit())
        subCommands.put(["whitelist", "wl"], new CWhitelist())
        subCommands.put(["blacklist", "bl"], new CBlacklist())
        subCommands.put(["moderators", "mods", "md"], new CModeration())
        subCommands.put(["public"], new CPublic())
        subCommands.put(["private"], new CPrivate())
        subCommands.put(["hide"], new CHide())
    }

    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {

        Optional<ServerVoiceChannel> potentialVoiceChannel = event.messageAuthor.connectedVoiceChannel
        if (potentialVoiceChannel.isEmpty()) {
            event.channel.sendMessage(l("cmd.config.error.not-in-voice-channel", event.server.get()))
            return
        }
        ServerConfig serverConfig = ConfigUtils.getServerConfig(event.server.get())
        ServerVoiceChannel voiceChannel = potentialVoiceChannel.get()
        Optional<PrivateChannel> potentialPrivateChannel = PrivateChannel.getFromChannel(
                config,
                serverConfig,
                event.server.get(),
                voiceChannel
        )
        if (potentialPrivateChannel.isEmpty() || voiceChannel.category.get().idAsString != serverConfig.categoryId) {
            event.channel.sendMessage(l("cmd.config.error.not-a-private-room", event.server.get()))
            return
        }
        PrivateChannel privateRoom = potentialPrivateChannel.get()
        if (!privateRoom.moderators.contains(event.messageAuthor.id)
                && !CommandUtils.hasPermission(config, event.messageAuthor.asUser().get(), event.server.get(), PermissionType.ADMINISTRATOR)
                && !CommandUtils.isAdmin(config, event.messageAuthor.asUser().get())) {
            event.channel.sendMessage(l("cmd.config.error.not-moderator", event.server.get()))
            return
        }

        CommandManager.repartSub(subCommands, event, cmd, config, args)
    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor(description: l("cmd.config.description", guild), usage: "<subcommand>")
    }

}
