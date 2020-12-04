package org.reddev.privateroomsreborn.utils.channels

import org.javacord.api.DiscordApi
import org.javacord.api.entity.channel.Channel
import org.javacord.api.entity.channel.ChannelCategory
import org.javacord.api.entity.channel.ServerChannel
import org.javacord.api.entity.channel.ServerChannelBuilder
import org.javacord.api.entity.channel.ServerVoiceChannel
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.permission.Permissions
import org.javacord.api.entity.server.Server
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.ServerConfig
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

class PrivateChannel {

    long channelId, serverId
    String name
    int userLimit
    List<Long> whitelistedUsers, blacklistedUsers, whitelistedRoles, blacklistedRoles
    boolean hidden, private_
    List<Long> moderators;

    def create(DiscordApi api) {
        Server server = api.getServerById(serverId).get()
        ServerConfig config = ConfigUtils.getServerConfig(server)
        ChannelCategory category = server.getChannelCategoryById(config.categoryId).get()
        server.createVoiceChannelBuilder().setCategory(category).setName(name).setUserlimit(userLimit).create().thenAccept { channel ->
            whitelistedUsers.forEach { userId ->
                //TODO Récupérer l'utilisateur et lui donner les droits
            }
            blacklistedUsers.forEach { userId ->
                //TODO Récupérer l'utilisateur et lui enlever les droits
            }
        }
    }

    def update() {
        ServerChannel channel = api.getServerById(serverId).get().getChannelById(channelId).get()

    }

    def delete(DiscordApi api, String reason = "No reason provided") {
        ServerChannel channel = api.getServerById(serverId).get().getChannelById(channelId).get()
        channel.delete(reason)
    }

    static Optional<PrivateChannel> getFromChannel(ServerConfig config, Server guild, ServerVoiceChannel channel) {
        if (channel.category.isEmpty() || (channel.category.get().idAsString != config.categoryId)) {
            return Optional.empty()
        }

        String name = channel.name.stripMargin().stripIndent()
        int userLimit = channel.userLimit.orElse(99)
        List<Long> whitelistedUsers, blacklistedUsers, whitelistedRoles, blacklistedRoles, moderators
        boolean hidden = false, private_ = false

        /*
         * Permissions :
         *  - Channel moderator : MOVE_MEMBERS
         *  - Whitelisted Roles / Members : CONNECT ; ! (!SPEAK) ; PRIORITY_SPEAKER
         *  - Blacklisted Roles / Members : ! READ_MESSAGES
         */

        channel.overwrittenRolePermissions.forEach { roleId, permission ->
            if (!guild.getAllowedPermissions(guild.everyoneRole.users[0]).contains(PermissionType.CONNECT)) {
                hidden = true
                if (!guild.getAllowedPermissions(guild.everyoneRole.users[0]).contains(PermissionType.READ_MESSAGES)) {
                    private_ = true
                }
            }
            if (permission.allowedPermission.contains(PermissionType.CONNECT)
                    && !permission.deniedPermissions.contains(PermissionType.SPEAK)
                    && permission.allowedPermission.contains(PermissionType.PRIORITY_SPEAKER)) {
                whitelistedRoles.add(roleId)
            }
            if (permission.deniedPermissions.contains(PermissionType.READ_MESSAGES) && !whitelistedRoles.contains(roleId)) {
                blacklistedRoles.add(roleId)
            }
        }
        channel.overwrittenUserPermissions.forEach { userId, permission ->
            if (permission.allowedPermission.contains(PermissionType.MOVE_MEMBERS)) {
                moderators.add(userId)
            }
            if (permission.allowedPermission.contains(PermissionType.CONNECT)
                    && !permission.deniedPermissions.contains(PermissionType.SPEAK)
                    && permission.allowedPermission.contains(PermissionType.PRIORITY_SPEAKER)
                    && !blacklistedUsers.contains(userId)) {
                whitelistedUsers.add(userId)
            }
            if (permission.deniedPermissions.contains(PermissionType.READ_MESSAGES) && !blacklistedUsers.contains(userId)) {
                blacklistedUsers.add(userId)
            }
        }

        return Optional.of(new PrivateChannel(channelId: channel.id, serverId: channel.server.id, blacklistedUsers: blacklistedUsers, blacklistedRoles: blacklistedRoles, whitelistedUsers: whitelistedUsers, whitelistedRoles: whitelistedRoles, userLimit: userLimit, moderators: moderators, name: name, hidden: hidden, private_: private_,))
    }

}

