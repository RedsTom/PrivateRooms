package org.reddev.privateroomsreborn.utils.general


import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.ServerConfig

import java.awt.Color

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l
import static org.reddev.privateroomsreborn.utils.general.StringUtils.j

class CommandUtils {

    static boolean matchPrefix(BotConfig config, String message, Server guild) {
        return message.startsWith(getPrefix(config, guild))
    }

    static String getPrefix(BotConfig config, Server guild) {
        ServerConfig serverConfig = ConfigUtils.getServerConfig(guild)
        String serverPrefix = serverConfig.getCustomPrefix(config)
        if (serverPrefix.isEmpty()) {
            serverPrefix = config.defaultPrefix
        }
        return serverPrefix
    }

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
            bitmask = bitmask + permissionType.value
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
}
