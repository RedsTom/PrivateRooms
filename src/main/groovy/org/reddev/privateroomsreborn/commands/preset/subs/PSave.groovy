package org.reddev.privateroomsreborn.commands.preset.subs

import com.google.gson.Gson
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.reddev.privateroomsreborn.Main
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor
import org.reddev.privateroomsreborn.api.commands.TCommand
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.channels.PrivateChannel
import org.reddev.privateroomsreborn.utils.general.CommandUtils
import org.reddev.privateroomsreborn.utils.general.ConfigUtils

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l
import static org.reddev.privateroomsreborn.utils.general.StringUtils.j

class PSave implements TCommand {

    @Override
    void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {

        if (args.length == 0) {
            CommandUtils.sendBadUsage(event, cmd, this)
            return
        }

        PrivateChannel channel = PrivateChannel.getFromChannel(config,
                ConfigUtils.getServerConfig(event.server.get()),
                event.server.get(),
                event.messageAuthor.connectedVoiceChannel.get()).get()

        Gson gson = Main.GSON
        String jsonOutput = gson.toJson(channel)
        if (ConfigUtils.saveTemplate(gson, event.messageAuthor, args.join(" "), jsonOutput)) {
            event.channel.sendMessage(j(l("cmd.preset.save.success", event.server.get()), args.join(" ")))
        } else {
            event.channel.sendMessage(j(l("cmd.preset.save.error.already-exists", event.server.get()), args.join(" ")))
        }
    }

    @Override
    CommandDescriptor getDescriptor(Server guild) {
        return new CommandDescriptor(usage: "<name>", description: l("cmd.preset.save.description", guild))
    }
}
