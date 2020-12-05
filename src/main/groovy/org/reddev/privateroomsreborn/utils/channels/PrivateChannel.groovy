package org.reddev.privateroomsreborn.utils.channels

import org.javacord.api.DiscordApi
import org.javacord.api.entity.channel.ChannelCategory
import org.javacord.api.entity.channel.ServerChannel
import org.javacord.api.entity.channel.ServerVoiceChannel
import org.javacord.api.entity.channel.ServerVoiceChannelUpdater
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.permission.Permissions
import org.javacord.api.entity.permission.Role
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.reddev.privateroomsreborn.utils.ServerConfig
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

import java.util.function.Consumer

import static org.reddev.privateroomsreborn.utils.general.CommandUtils.bitmask

class PrivateChannel {

    long channelId, serverId
    String name
    int userLimit
    List<Long> whitelistedUsers, blacklistedUsers, whitelistedRoles, blacklistedRoles
    boolean hidden, private_
    List<Long> moderators

    def create(DiscordApi api, Consumer<Void> then = { it -> }) {
        Server server = api.getServerById(serverId).get()
        ServerConfig config = ConfigUtils.getServerConfig(server)
        ChannelCategory category = server.getChannelCategoryById(config.categoryId).get()
        server.createVoiceChannelBuilder().setCategory(category).setName(name).setUserlimit(userLimit).create().thenAccept {
            this.channelId = it.id
            update(api, then)
        }
    }

    def update(DiscordApi api, Consumer<Void> then = { it -> }) {
        Server server = api.getServerById(serverId).get()
        ServerVoiceChannel channel = server.getVoiceChannelById(channelId).get()
        this.channelId = channel.id
        ServerVoiceChannelUpdater updater = channel.createUpdater()
        updater.setName(name)
        updater.setUserLimit(userLimit)
        if (private_) {
            updater.addPermissionOverwrite(server.everyoneRole, Permissions.fromBitmask(bitmask(), bitmask(PermissionType.CONNECT)))
        }
        if (hidden) {
            updater.addPermissionOverwrite(server.everyoneRole, Permissions.fromBitmask(bitmask(), bitmask(PermissionType.READ_MESSAGES)))
        }
        whitelistedUsers.forEach { userId ->
            User user = server.getMemberById(userId).get()
            updater.addPermissionOverwrite(user, Permissions.fromBitmask(bitmask(PermissionType.CONNECT, PermissionType.PRIORITY_SPEAKER), bitmask()))
        }
        blacklistedUsers.forEach { userId ->
            User user = server.getMemberById(userId).get()
            updater.addPermissionOverwrite(user,
                    Permissions.fromBitmask(
                            bitmask(),
                            bitmask(
                                    PermissionType.READ_MESSAGES
                            )
                    )
            )
        }
        whitelistedRoles.forEach { roleId ->
            Role role = server.getRoleById(roleId).get()
            updater.addPermissionOverwrite(role,
                    Permissions.fromBitmask(bitmask(
                            PermissionType.CONNECT,
                            PermissionType.PRIORITY_SPEAKER),
                            bitmask()
                    ))

        }
        blacklistedRoles.forEach { roleId ->
            Role role = server.getRoleById(roleId).get()
            updater.addPermissionOverwrite(role,
                    Permissions.fromBitmask(
                            bitmask(),
                            bitmask(
                                    PermissionType.READ_MESSAGES
                            )
                    )
            )
        }
        moderators.forEach { userId ->
            User user = server.getMemberById(userId).get()
            updater.addPermissionOverwrite(user,
                    Permissions.fromBitmask(bitmask(
                            PermissionType.MOVE_MEMBERS),
                            bitmask()
                    )
            )
        }

        updater.update().thenAccept {
            then.accept()
        }
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
        List<Long> whitelistedUsers = [],
                   blacklistedUsers = [],
                   whitelistedRoles = [],
                   blacklistedRoles = [],
                   moderators = []
        boolean hidden = false, private_ = false

        /*
         * Permissions :
         *  - Channel moderator : MOVE_MEMBERS
         *  - Whitelisted Roles / Members : CONNECT ; ! (!SPEAK) ; PRIORITY_SPEAKER
         *  - Blacklisted Roles / Members : ! READ_MESSAGES
         */

        channel.overwrittenRolePermissions.forEach { roleId, permission ->
            if (roleId == guild.everyoneRole.id) {
                if (permission.deniedPermissions.contains(PermissionType.CONNECT)) {
                    private_ = true
                    if (permission.deniedPermissions.contains(PermissionType.READ_MESSAGES)) {
                        hidden = true
                    }
                }
            } else {
                if (permission.allowedPermission.contains(PermissionType.CONNECT)
                        && !permission.deniedPermissions.contains(PermissionType.SPEAK)
                        && permission.allowedPermission.contains(PermissionType.PRIORITY_SPEAKER)
                        && !blacklistedRoles.contains(roleId)) {
                    whitelistedRoles.add(roleId)
                }
                if (permission.deniedPermissions.contains(PermissionType.CONNECT)
                        && !whitelistedRoles.contains(roleId)) {
                    blacklistedRoles.add(roleId)
                }
            }
        }
        channel.overwrittenUserPermissions.forEach { userId, permission ->
            if (permission.allowedPermission.contains(PermissionType.MOVE_MEMBERS)) {
                moderators.add(userId)
            }
            if (permission.deniedPermissions.contains(PermissionType.READ_MESSAGES)
                    && !blacklistedUsers.contains(userId)) {
                blacklistedUsers.add(userId)
            }
            if (permission.allowedPermission.contains(PermissionType.CONNECT)
                    && !permission.deniedPermissions.contains(PermissionType.SPEAK)
                    && permission.allowedPermission.contains(PermissionType.PRIORITY_SPEAKER)
                    && !blacklistedUsers.contains(userId)) {
                whitelistedUsers.add(userId)
            }
        }

        def pChannel = new PrivateChannel(
                channelId: channel.id,
                serverId: channel.server.id,
                blacklistedUsers: blacklistedUsers,
                blacklistedRoles: blacklistedRoles,
                whitelistedUsers: whitelistedUsers,
                whitelistedRoles: whitelistedRoles,
                userLimit: userLimit,
                moderators: moderators,
                name: name,
                hidden: hidden,
                private_: private_,
        )
        return Optional.of(pChannel
        )
    }


    @Override
    String toString() {
        return "PrivateChannel{" +
                "\nchannelId=" + channelId +
                ",\n serverId=" + serverId +
                ",\n name='" + name + '\'' +
                ",\n userLimit=" + userLimit +
                ",\n whitelistedUsers=" + whitelistedUsers +
                ",\n blacklistedUsers=" + blacklistedUsers +
                ",\n whitelistedRoles=" + whitelistedRoles +
                ",\n blacklistedRoles=" + blacklistedRoles +
                ",\n hidden=" + hidden +
                ",\n private_=" + private_ +
                ",\n moderators=" + moderators +
                '\n}'
    }
}

