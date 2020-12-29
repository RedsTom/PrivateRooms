package org.reddev.privateroomsreborn.commands.settings.subs

import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.ServerConfig
import org.reddev.privateroomsreborn.utils.general.CommandUtils
import org.reddev.privateroomsreborn.utils.general.ConfigUtils
import org.reddev.privateroomsreborn.utils.general.UnirestUtils

import java.awt.*

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l
import static org.reddev.privateroomsreborn.utils.general.StringUtils.j

class SLanguage implements TCommand {

    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
        ServerConfig sConfig = ConfigUtils.getServerConfig(event.server.get())
        if (args.length != 1) {
            CommandUtils.sendBadUsage(event, cmd, this)
            return
        }


        String language = args[0]

        if (!config.languages.contains(language)) {
            event.channel.sendMessage(new EmbedBuilder()
                    .setTitle(j("%s !", l("errors.error", event.server.get())))
                    .setDescription(l("cmd.settings.language.error.language-does-not-exists.description", event.server.get()))
                    .addField(l("cmd.settings.language.error.language-does-not-exists.possibilities", event.server.get()), j("`%s`", config.languages.join("`, `").replace("\"", "")))
                    .setColor(Color.RED)
            )
            return
        }
        sConfig.setLanguage(language)
        ConfigUtils.update(event.server.get(), sConfig)
        event.channel.sendMessage(new EmbedBuilder()
                .setTitle(l("cmd.settings.language.embed.title", event.server.get()))
                .setDescription(j("%s `%s`",
                        l("cmd.settings.language.embed.description", event.server.get()),
                        UnirestUtils.getCountryName(language).capitalize()
                ))
                .setThumbnail(UnirestUtils.getCountryFlag(language))
                .setColor(Color.GREEN)
        )
    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor(usage: "<lang>", description: l("cmd.settings.language.description", guild))
    }

}
