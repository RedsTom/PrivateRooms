package org.reddev.privateroomsreborn.commands.config.subs

import com.sun.org.apache.xpath.internal.operations.Mod
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.commands.gensubs.SubHelp
import org.reddev.privateroomsreborn.commands.utils.CommandManager
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.channels.PrivateChannel
import org.reddev.privateroomsreborn.utils.general.CommandUtils
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

import java.awt.Color

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l
import static org.reddev.privateroomsreborn.utils.general.StringUtils.j

class CModeration implements TCommand {

    private Map<List<String>, TCommand> subCommands = new HashMap<>()

    CModeration() {
        subCommands.put(["add", "+", "+u"], new ModUserAdd())
        subCommands.put(["remove", "-", "-u"], new ModUserRemove())
        subCommands.put(["show", "=", "list"], new ModShow())
        subCommands.put(["help", "?"], new SubHelp(cmds: subCommands))
    }

    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
        CommandManager.repartSub(subCommands, event, cmd, config, args)
    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor(usage: "<subcommand>", description: l("cmd.config.moderators.description", guild))
    }


    static class ModUserAdd implements TCommand {

        @Override
        void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
            PrivateChannel channel = PrivateChannel.getFromChannel(
                    config,
                    ConfigUtils.getServerConfig(event.server.get()),
                    event.server.get(),
                    event.messageAuthor.connectedVoiceChannel.get()
            ).get()

            Optional<User> potentialUser = CommandUtils.getUserFromMessage(event, this, cmd, args)
            if (!potentialUser.isPresent()) return
            User mentionedUser = potentialUser.get()
            channel.moderators.add(mentionedUser.id)
            channel.update(event.api).thenAccept {
                event.channel.sendMessage(j(l("cmd.config.moderators.add.success", event.server.get()), mentionedUser.discriminatedName))
            }
        }

        @Override
        CommandDescriptor getDescriptor(Server guild) {
            return new CommandDescriptor(description: l("cmd.config.moderators.add.description", guild), usage: "<id/@mention>")
        }
    }

    static class ModUserRemove implements TCommand {

        @Override
        void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
            PrivateChannel channel = PrivateChannel.getFromChannel(
                    config,
                    ConfigUtils.getServerConfig(event.server.get()),
                    event.server.get(),
                    event.messageAuthor.connectedVoiceChannel.get()
            ).get()

            Optional<User> potentialUser = CommandUtils.getUserFromMessage(event, this, cmd, args)
            if (!potentialUser.isPresent()) return
            User mentionedUser = potentialUser.get()
            channel.moderators.remove(mentionedUser.id)
            channel.update(event.api).thenAccept {
                event.channel.sendMessage(j(l("cmd.config.moderators.remove.success", event.server.get()), mentionedUser.discriminatedName))
                channel.clear(event.messageAuthor.connectedVoiceChannel.get(), mentionedUser)
            }
        }

        @Override
        CommandDescriptor getDescriptor(Server guild) {
            return new CommandDescriptor(description: l("cmd.config.moderators.remove.description", guild), usage: "<id/@mention>")
        }
    }

    static class ModShow implements TCommand {
        @Override
        void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {

            PrivateChannel channel = PrivateChannel.getFromChannel(
                    config,
                    ConfigUtils.getServerConfig(event.server.get()),
                    event.server.get(),
                    event.messageAuthor.connectedVoiceChannel.get()
            ).get()

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(l("cmd.config.moderators.show.title", event.server.get()))
                    .setColor(Color.GREEN)

            List<String> users = new ArrayList<>()
            channel.moderators.forEach { users.add(event.api.getUserById(it).get().discriminatedName) }
            embedBuilder.addField("Utilisateurs :", users.size() > 0 ? "`" + users.join("`, `") + "`" : l("cmd.config.moderators.show.no-value-present", event.server.get()))

            event.channel.sendMessage(embedBuilder)
        }

        @Override
        CommandDescriptor getDescriptor(Server guild) {
            return new CommandDescriptor(description: l("cmd.config.moderators.show.description", guild))
        }
    }
}
