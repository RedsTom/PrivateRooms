package org.reddev.privateroomsreborn.commands.preset.subs;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor;
import org.reddev.privateroomsreborn.api.commands.TCommand;
import org.reddev.privateroomsreborn.utils.BotConfig;
import org.reddev.privateroomsreborn.utils.general.ConfigUtils;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l;

public class PList implements TCommand {
    @Override
    public void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {

        File folder = ConfigUtils.getTemplateFolder(event.getMessageAuthor());

        if (folder == null || !folder.exists() || folder.listFiles().length == 0) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle(l("cmd.preset.list.title", event.getServer().get()))
                    .setDescription(l("cmd.preset.list.no-presets", event.getServer().get()))
                    .setColor(Color.GREEN);
            event.getChannel().sendMessage(eb);
            return;
        }

        List<String> templates = Stream.of(folder.listFiles())
                .map(File::getName)
                .filter(name -> name.endsWith(".json"))
                .map(s -> s.replace(".json", ""))
                .collect(Collectors.toList());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(l("cmd.preset.list.title", event.getServer().get()))
                .setDescription("`" + String.join("`, `", templates) + "`")
                .setColor(Color.GREEN);

        event.getChannel().sendMessage(embedBuilder);
    }

    @Override
    public CommandDescriptor getDescriptor(Server guild) {
        CommandDescriptor desc = new CommandDescriptor();
        desc.setDescription(l("cmd.preset.list.description", guild));

        return desc;
    }
}
