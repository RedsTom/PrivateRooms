package org.reddev.privateroomsreborn.commands.preset.subs;

import com.google.gson.Gson;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.reddev.privateroomsreborn.Main;
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor;
import org.reddev.privateroomsreborn.api.commands.TCommand;
import org.reddev.privateroomsreborn.utils.BotConfig;
import org.reddev.privateroomsreborn.utils.channels.PrivateChannel;
import org.reddev.privateroomsreborn.utils.general.CommandUtils;
import org.reddev.privateroomsreborn.utils.general.ConfigUtils;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l;
import static org.reddev.privateroomsreborn.utils.general.StringUtils.j;

public class PDelete implements TCommand {
    @Override
    public void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
        if (args.length == 0) {
            CommandUtils.sendBadUsage(event, cmd, this);
            return;
        }

        String name = String.join(" ", args);

        File file = ConfigUtils.getConfigFile(event.getMessageAuthor().asUser().get(), name);

        if (file == null) {
            event.getChannel().sendMessage(j(l("cmd.preset.delete.error.not-found", event.getServer().get()), name));
            return;
        }

        if (!file.delete()) {
            event.getChannel().sendMessage(j(l("cmd.preset.delete.error.generic", event.getServer().get()), name));
            return;
        }

        event.getChannel().sendMessage(j(l("cmd.preset.delete.success", event.getServer().get()), name));
    }

    @Override
    public CommandDescriptor getDescriptor(Server guild) {
        CommandDescriptor desc = new CommandDescriptor();
        desc.setDescription(l("cmd.preset.delete.description", guild));

        return desc;
    }
}
