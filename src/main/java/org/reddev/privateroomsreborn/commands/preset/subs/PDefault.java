package org.reddev.privateroomsreborn.commands.preset.subs;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.reddev.privateroomsreborn.api.commands.CommandDescriptor;
import org.reddev.privateroomsreborn.api.commands.TCommand;
import org.reddev.privateroomsreborn.commands.gensubs.SubHelp;
import org.reddev.privateroomsreborn.commands.utils.CommandManager;
import org.reddev.privateroomsreborn.utils.BotConfig;
import org.reddev.privateroomsreborn.utils.general.CommandUtils;
import org.reddev.privateroomsreborn.utils.general.ConfigUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

import static org.reddev.privateroomsreborn.utils.general.LangUtils.l;

public class PDefault implements TCommand {

    private final Map<List<String>, TCommand> subCommands;

    public PDefault() {
        subCommands = new HashMap<>();

        SubHelp help = new SubHelp();
        help.setCmds(subCommands);
        help.setCmdName("preset default");

        subCommands.put(Arrays.asList("help", "?"), help);
        subCommands.put(Collections.singletonList("set"), new Set());
        subCommands.put(Arrays.asList("disable", "reset"), new Reset());
    }

    @Override
    public void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
        CommandManager.repartSub(subCommands, event, cmd, config, args);
    }

    @Override
    public CommandDescriptor getDescriptor(Server guild) {
        CommandDescriptor descriptor = new CommandDescriptor();
        descriptor.setDescription(l("cmd.preset.default.description", guild));
        descriptor.setUsage("<subcommand>");

        return descriptor;
    }

    private static class Reset implements TCommand {
        @Override
        public void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
            File folder = ConfigUtils.getTemplateFolder(event.getMessageAuthor().asUser().get());
            Path defaultFile = folder.toPath().resolve("default.json");

            try {
                Files.delete(defaultFile);
            } catch (IOException e) {
                EmbedBuilder eb = new EmbedBuilder()
                        .setTitle(l("cmd.preset.default.reset.title", event.getServer().get()))
                        .setDescription(l("cmd.preset.default.reset.no-default", event.getServer().get()))
                        .setColor(Color.RED);

                event.getChannel().sendMessage(eb);
                return;
            }

            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle(l("cmd.preset.default.reset.title", event.getServer().get()))
                    .setDescription(l("cmd.preset.default.reset.success", event.getServer().get()))
                    .setColor(Color.GREEN);

            event.getChannel().sendMessage(eb);
        }

        @Override
        public CommandDescriptor getDescriptor(Server guild) {
            CommandDescriptor descriptor = new CommandDescriptor();
            descriptor.setDescription(l("cmd.preset.default.reset.description", guild));

            return descriptor;
        }
    }

    private static class Set implements TCommand {
        @Override
        public void execute(MessageCreateEvent event, BotConfig config, String cmd, String[] args) {
            if (args.length != 1) {
                CommandUtils.sendBadUsage(event, cmd, this);
                return;
            }
            Server server = event.getServer().get();

            File configFile = ConfigUtils.getConfigFile(event.getMessageAuthor().asUser().get(), args[0]);
            if (configFile == null || !configFile.exists()) {
                EmbedBuilder eb = new EmbedBuilder()
                        .setTitle(l("cmd.preset.default.set.title", server))
                        .setDescription(l("cmd.preset.default.set.not-found", server))
                        .setColor(Color.RED);
                event.getChannel().sendMessage(eb);
                return;
            }

            File folder = ConfigUtils.getTemplateFolder(event.getMessageAuthor().asUser().get());
            Path defaultFile = folder.toPath().resolve("default.json");

            try {
                Files.createSymbolicLink(defaultFile, configFile.toPath());
            } catch (IOException e) {
                EmbedBuilder eb = new EmbedBuilder()
                        .setTitle(l("cmd.preset.default.set.title", server))
                        .setDescription(l("cmd.preset.default.set.error", server))
                        .setColor(Color.RED);

                event.getChannel().sendMessage(eb);
                return;
            }

            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle(l("cmd.preset.default.set.title", server))
                    .setDescription(l("cmd.preset.default.set.success", server))
                    .setColor(Color.GREEN);
            event.getChannel().sendMessage(eb);
        }

        @Override
        public CommandDescriptor getDescriptor(Server guild) {
            CommandDescriptor descriptor = new CommandDescriptor();
            descriptor.setUsage("<name>");
            descriptor.setDescription(l("cmd.preset.default.set.description", guild));

            return descriptor;
        }
    }
}
