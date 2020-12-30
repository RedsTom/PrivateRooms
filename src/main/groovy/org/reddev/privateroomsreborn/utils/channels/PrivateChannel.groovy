package org.reddev.privateroomsreborn.utils.channels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
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
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.ServerConfig
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

import java.util.concurrent.CompletableFuture

import static org.reddev.privateroomsreborn.utils.general.CommandUtils.bitmask

class PrivateChannel {

    long channelId

    @Expose
    long serverId
    @Expose
    String name
    @Expose
    int userLimit = 99
    @Expose
    List<Long> whitelistedUsers = [], blacklistedUsers = [], whitelistedRoles = [], blacklistedRoles = [], moderators = []
    @Expose
    boolean hidden = false
    @Expose
    @SerializedName("private")
    boolean private_ = false

    /**
     * Method that creates the channel with default parameters
     * @param api Discord bot's instance
     * @return The CompletableFuture to execute actions after
     */
    CompletableFuture<Void> create(DiscordApi api) {
        Server server = api.getServerById(serverId).get()

        server.createVoiceChannelBuilder().setName("Channel creating...").create().thenAccept({
            this.channelId = it.id
            this.update(api)
        })
    }

    /**
     * Method that configures the channel with the correct params
     * @param api Discord bot's instance
     * @return The CompletableFuture to execute actions after
     */
    CompletableFuture<Void> update(DiscordApi api) {
        Server server = api.getServerById(serverId).get()
        ServerVoiceChannel channel = server.getVoiceChannelById(channelId).get()
        this.channelId = channel.id

        ServerVoiceChannelUpdater updater = channel.createUpdater()

        ServerConfig config = ConfigUtils.getServerConfig(server)
        ChannelCategory category = server.getChannelCategoryById(config.categoryId).get()
        updater.setCategory(category)
        updater.setName(name)
        updater.setUserLimit(userLimit)
        if (private_) {
            updater.addPermissionOverwrite(server.everyoneRole, Permissions.fromBitmask(bitmask(), bitmask(PermissionType.CONNECT)))
        }
        if (hidden) {
            updater.addPermissionOverwrite(server.everyoneRole, Permissions.fromBitmask(bitmask(), bitmask(PermissionType.READ_MESSAGES)))
        }
        whitelistedUsers.forEach { userId ->
            User user = api.getUserById(userId).get()
            updater.addPermissionOverwrite(user, Permissions.fromBitmask(bitmask(PermissionType.CONNECT, PermissionType.PRIORITY_SPEAKER), bitmask()))
        }
        blacklistedUsers.forEach { userId ->
            User user = api.getUserById(userId).get()
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
            User user = api.getUserById(userId).get()
            updater.addPermissionOverwrite(user,
                    Permissions.fromBitmask(bitmask(
                            PermissionType.MOVE_MEMBERS),
                            bitmask()
                    )
            )
        }
        return updater.update()
    }


    /**
     * A method to delete the {@link PrivateChannel}
     * @param api Discord bot's instance
     * @param reason Reason of the deletion
     */
    void delete(DiscordApi api, String reason = "No reason provided") {
        ServerChannel channel = api.getServerById(serverId).get().getChannelById(channelId).get()
        channel.delete(reason)
    }

    /**
     * A method to get a {@link PrivateChannel} from a {@link ServerVoiceChannel}
     * @param bConfig Config of the bot
     * @param config Config of the server
     * @param guild Guild of the channel
     * @param channel Channel of the PrivateRoom to get
     * @return The{@link PrivateChannel} got from the {@link ServerVoiceChannel}
     */
    static Optional<PrivateChannel> getFromChannel(BotConfig bConfig, ServerConfig config, Server guild, ServerVoiceChannel channel) {
        if (!channel.category.isPresent() || (channel.category.get().idAsString != config.categoryId)) {
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
                        && permission.allowedPermission.contains(PermissionType.PRIORITY_SPEAKER)) {
                    whitelistedRoles.add(roleId)
                }
                if (permission.deniedPermissions.contains(PermissionType.CONNECT)) {
                    blacklistedRoles.add(roleId)
                }
            }
        }
        channel.overwrittenUserPermissions.forEach { userId, permission ->
            if (permission.allowedPermission.contains(PermissionType.MOVE_MEMBERS)) {
                moderators.add(userId)
            }
            if (permission.deniedPermissions.contains(PermissionType.READ_MESSAGES)) {
                blacklistedUsers.add(userId)
            }
            if (permission.allowedPermission.contains(PermissionType.CONNECT)
                    && !permission.deniedPermissions.contains(PermissionType.SPEAK)
                    && permission.allowedPermission.contains(PermissionType.PRIORITY_SPEAKER)) {
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
        return Optional.of(pChannel)
    }

    /**
     * Resets the channel options
     */
    void reset() {
        userLimit = 99
        name = ""
        whitelistedUsers = []
        whitelistedRoles = []
        blacklistedUsers = []
        blacklistedRoles = []
        moderators = []
        hidden = false
        private_ = false
    }

    /**
     * Permits to print the channel
     * @return The channel stringified
     */
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

