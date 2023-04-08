package org.reddev.privateroomsreborn.commands.preset

import org.javacord.api.entity.channel.ServerVoiceChannel
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.commands.gensubs.SubHelp
import org.reddev.privateroomsreborn.commands.preset.subs.PList
import org.reddev.privateroomsreborn.commands.preset.subs.PLoad
import org.reddev.privateroomsreborn.commands.preset.subs.PSave
import org.reddev.privateroomsreborn.commands.utils.CommandManager
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.ServerConfig
import org.reddev.privateroomsreborn.utils.channels.PrivateChannel
import org.reddev.privateroomsreborn.utils.general.CommandUtils
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l

class CommandPreset implements TCommand {

    private Map<List<String>, TCommand> subCommands

    CommandPreset() {
        subCommands = new HashMap<>()
        subCommands.put(["help", "?"], new SubHelp(cmds: subCommands, cmdName: "preset"))
        subCommands.put(["save"], new PSave())
        subCommands.put(["load"], new PLoad())
        subCommands.put(["list", "=", "show"], new PList())
    }

    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {

        Optional<ServerVoiceChannel> potentialVoiceChannel = event.messageAuthor.connectedVoiceChannel
        if (!potentialVoiceChannel.isPresent()) {
            event.channel.sendMessage(l("cmd.preset.error.not-in-voice-channel", event.server.get()))
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
        if (!potentialPrivateChannel.isPresent() || voiceChannel.category.get().idAsString != serverConfig.categoryId) {
            event.channel.sendMessage(l("cmd.preset.error.not-a-private-room", event.server.get()))
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
        return new CommandDescriptor(description: l("cmd.preset.description", guild), usage: "<subcommand>")
    }
}
