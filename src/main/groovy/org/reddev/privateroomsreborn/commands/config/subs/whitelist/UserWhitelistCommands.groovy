package org.reddev.privateroomsreborn.commands.config.subs.whitelist


import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.channels.PrivateChannel
import org.reddev.privateroomsreborn.utils.general.CommandUtils
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l
import static org.reddev.privateroomsreborn.utils.general.StringUtils.j

class UserWhitelistCommands {

    static class WLUserAdd implements TCommand {

        @Override
        void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
            PrivateChannel channel = PrivateChannel.getFromChannel(
                    config,
                    ConfigUtils.getServerConfig(event.server.get()),
                    event.server.get(),
                    event.messageAuthor.connectedVoiceChannel.get()
            ).get()

            Optional<User> potentialUser = CommandUtils.getUserFromMessage(event, this, cmd, args)
            if (potentialUser.isEmpty()) return
            User mentionedUser = potentialUser.get()
            channel.whitelistedUsers.add(mentionedUser.id)
            channel.update(event.api).thenAccept {
                event.channel.sendMessage(j(l("cmd.config.whitelist.add.user.success", event.server.get()), mentionedUser.mentionTag))
            }
        }

        @Override
        CommandDescriptor getDescriptor(Server guild) {
            return new CommandDescriptor(description: l("cmd.config.whitelist.add.user.description", guild), usage: "<id/@mention>")
        }
    }

    static class WLUserRemove implements TCommand {

        @Override
        void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
            PrivateChannel channel = PrivateChannel.getFromChannel(
                    config,
                    ConfigUtils.getServerConfig(event.server.get()),
                    event.server.get(),
                    event.messageAuthor.connectedVoiceChannel.get()
            ).get()

            Optional<User> potentialUser = CommandUtils.getUserFromMessage(event, this, cmd, args)
            if (potentialUser.isEmpty()) return
            User mentionedUser = potentialUser.get()
            channel.whitelistedUsers.add(mentionedUser.id)
            channel.update(event.api).thenAccept {
                event.channel.sendMessage(j(l("cmd.config.whitelist.remove.user.success", event.server.get()), mentionedUser.mentionTag))
            }

        }

        @Override
        CommandDescriptor getDescriptor(Server guild) {
            return new CommandDescriptor(description: l("cmd.config.whitelist.remove.user.description", guild), usage: "<id/@mention>")
        }
    }


}
