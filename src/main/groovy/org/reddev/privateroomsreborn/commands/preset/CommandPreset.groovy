package org.reddev.privateroomsreborn.commands.preset

import org.javacord.api.entity.channel.ServerVoiceChannel
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.ICommand
import org.reddev.privateroomsreborn.commands.gensubs.SSubHelp
import org.reddev.privateroomsreborn.commands.preset.subs.SSubLoad
import org.reddev.privateroomsreborn.commands.preset.subs.SSubSave
import org.reddev.privateroomsreborn.commands.utils.CommandManager
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.ServerConfig
import org.reddev.privateroomsreborn.utils.channels.PrivateChannel
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l

class CommandPreset implements ICommand {

    private Map<List<String>, ICommand> subCommands

    CommandPreset() {
        subCommands = new HashMap<>()
        subCommands.put(["help", "?"], new SSubHelp(cmds: subCommands, cmdName: "preset"))
        subCommands.put(["save"], new SSubSave())
        subCommands.put(["load"],new SSubLoad())
    }

    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {

        Optional<ServerVoiceChannel> potentialVoiceChannel = event.messageAuthor.connectedVoiceChannel
        if (potentialVoiceChannel.isEmpty()) {
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
        if (potentialPrivateChannel.isEmpty() || voiceChannel.category.get().idAsString != serverConfig.categoryId) {
            event.channel.sendMessage(l("cmd.preset.error.not-a-private-room", event.server.get()))
            return
        }
        PrivateChannel privateRoom = potentialPrivateChannel.get()
        if (!privateRoom.moderators.contains(event.messageAuthor.id)) {
            event.channel.sendMessage(l("cmd.preset.error.not-moderator", event.server.get()))
            return
        }

        CommandManager.repartSub(subCommands, event, cmd, config, args)

    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor()
    }
}