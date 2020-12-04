package org.reddev.privateroomsreborn.utils.general

import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.permission.Permissions
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.ServerConfig

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
        int bitmask = 0x0
        for (def permissionType in permissionTypes) {
            bitmask = bitmask + permissionType.value
        }

        return (guild.getPermissions(user).allowedBitmask & bitmask) == bitmask
    }

    static boolean isAdmin(BotConfig config, User user) {
        def rt = false
        config.botOps.forEach { id, discriminator ->
            if (user.idAsString == id && user.discriminator == discriminator)
                rt = true
        }
        return rt
    }

}
