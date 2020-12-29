package org.reddev.privateroomsreborn.commands

import org.javacord.api.entity.channel.ChannelCategory
import org.javacord.api.entity.channel.ChannelCategoryBuilder
import org.javacord.api.entity.channel.ServerVoiceChannel
import org.javacord.api.entity.channel.ServerVoiceChannelBuilder
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.ServerConfig
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l

class CommandSetup implements
        TCommand {

    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {

        init(event.server.get())
        event.channel.sendMessage(l("cmd.setup.success", event.server.get()))

    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor(permissions: [PermissionType.MANAGE_CHANNELS], description: l("cmd.setup.description", guild))
    }

    static def init(Server guild) {
        delete(guild)

        ServerConfig config = ConfigUtils.getServerConfig(guild)

        ChannelCategoryBuilder categoryBuilder = guild.createChannelCategoryBuilder()
                .setName(l("settings.category-name", guild))

        ChannelCategory category = categoryBuilder.create().get()
        config.categoryId = category.idAsString

        ServerVoiceChannelBuilder channelBuilder = guild.createVoiceChannelBuilder()
                .setName(l("settings.create-channel-name", guild))
                .setUserlimit(1)
                .setCategory(category)
        ServerVoiceChannel channel = channelBuilder.create().get()
        config.createChannelId = channel.idAsString

        config.deleted = false

        ConfigUtils.update(guild, config)
    }

    static def delete(Server guild) {
        ServerConfig config = ConfigUtils.getServerConfig(guild)

        if (config.categoryId || config.createChannelId) {
            Optional<ChannelCategory> category = guild.getChannelCategoryById(config.categoryId)
            category.ifPresent {
                it.channels.forEach { if (it instanceof ServerVoiceChannel && !config.whitelistedVoiceChannels.contains(it.id)) it.delete() }
                it.delete()
            }
            Optional<ServerVoiceChannel> channel = guild.getVoiceChannelById(config.createChannelId)
            channel.ifPresent { it.delete() }
        }

        ConfigUtils.update(guild, config)
    }
}
