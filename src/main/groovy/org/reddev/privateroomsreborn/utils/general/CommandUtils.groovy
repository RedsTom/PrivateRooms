package org.reddev.privateroomsreborn.utils.general

import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.ServerConfig

import java.awt.*

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l
import static org.reddev.privateroomsreborn.utils.general.StringUtils.j

class CommandUtils {

    /**
     * Returns if the message given is a command
     * @param config Configuration of the bot
     * @param message Message content to check
     * @param guild Guild where to check
     * @return If the given message is a command
     */
    static boolean matchPrefix(BotConfig config, String message, Server guild) {
        return message.startsWith(getPrefix(config, guild))
    }

    /**
     * Returns the prefix of the bot on a server
     * @param config Configuration of the bot
     * @param guild Guild where to check the prefix
     * @return The prefix of the bot on the guild
     */
    static String getPrefix(BotConfig config, Server guild) {
        ServerConfig serverConfig = ConfigUtils.getServerConfig(guild)
        String serverPrefix = serverConfig.getCustomPrefix(config)
        if (serverPrefix.isEmpty()) {
            serverPrefix = config.defaultPrefix
        }
        return serverPrefix
    }

    /**
     * Returns if the user has to provided permissions with the bitmask and bypass if the user is admin
     * @param config Configuration of the bot
     * @param user User to check the permissions
     * @param guild Guild where to check the permissions
     * @param permissionTypes PermissionTypes to check
     * @return If the user has the provided permissions
     */
    static boolean hasPermission(BotConfig config, User user, Server guild, PermissionType... permissionTypes) {
        if (isAdmin(config, user)) {
            return true
        }
        def bitmask = bitmask(permissionTypes)

        return (guild.getPermissions(user).allowedBitmask & bitmask) == bitmask
    }

    static int bitmask(PermissionType... permissionTypes) {
        int bitmask = 0x0
        for (def permissionType in permissionTypes) {
            bitmask = (bitmask + permissionType.value).toInteger()
        }
        return bitmask
    }

    static boolean isAdmin(BotConfig config, User user) {
        def rt = false
        config.botOps.forEach { id, discriminator ->
            if (user.idAsString == id && user.discriminator == discriminator)
                rt = true
        }
        return rt
    }

    /**
     * Sends the good usage of the command on the event's channel
     * @param event Event of the command
     * @param cmd Name of the command
     * @param command Command where executing
     */
    static void sendBadUsage(MessageCreateEvent event, String cmd, TCommand command) {
        event.channel.sendMessage(
                new EmbedBuilder()
                        .setTitle(l("errors.incorrect-syntax", event.server.get()))
                        .setColor(Color.RED)
                        .addField(
                                j("``%s %s``",
                                        cmd,
                                        command.getDescriptor(event.server.get()).usage), "** **")
        )
    }

    /**
     * Gets an user from a given message
     * @param message Message where to get user
     * @param parsedArgs Argument parsed by the command parser
     * @return The user if there is one found
     */
    static Optional<User> getUserFromMessage(MessageCreateEvent event, TCommand command, String cmdName, String[] args) {
        // If there is no mentions or if there is no arguments
        if (event.message.mentionedUsers.size() == 0 && args.length == 0) {
            sendBadUsage(event, cmdName, command)
            return Optional.empty()
        }
        // If there is more than one mention or if there is more than one argument
        if (event.message.mentionedUsers.size() > 1 || args.length >= 2) {
            sendBadUsage(event, cmdName, command)
            return Optional.empty()
        }
        // If there is a mention
        if (event.message.mentionedUsers.size() == 1) {
            return Optional.of(event.message.mentionedUsers.get(0))
        }

        // If there's not : search for the user
        Optional<User> potentialUser = event.server.get().getMemberById(args[0])
        if (!potentialUser.isPresent()) {
            event.channel.sendMessage(j(l("cmd.config.error.cannot-find-user", event.server.get()), args[0]))
            return Optional.empty()
        }
        return potentialUser
    }
}
