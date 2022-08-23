package me.redstom.privaterooms.commands;

import lombok.RequiredArgsConstructor;
import me.redstom.privaterooms.db.entity.Template;
import me.redstom.privaterooms.db.entity.User;
import me.redstom.privaterooms.db.services.TemplateService;
import me.redstom.privaterooms.db.services.UserService;
import me.redstom.privaterooms.util.command.ICommand;
import me.redstom.privaterooms.util.command.RegisterCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;
import java.util.function.Function;

@RegisterCommand
@RequiredArgsConstructor
public class TemplateCommand implements ICommand {

    private final TemplateService templateService;
    private final UserService userService;

    @Override
    public CommandData command() {
        return Commands.slash("template", "Manage channel templates")
          .setGuildOnly(true)
          .addSubcommands(
            new SubcommandData("create", "Create a new template")
              .addOption(OptionType.STRING, "name", "The name of the template", true),
            new SubcommandData("delete", "Delete a template")
              .addOption(OptionType.STRING, "name", "The name of the template", true, true),
            new SubcommandData("load", "Load a template")
              .addOption(OptionType.STRING, "name", "The name of the template", true, true),
            new SubcommandData("list", "List all templates")
          );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        switch (event.getSubcommandName()) {
            case "create" -> this.create(event);
            case "delete" -> this.delete(event);
            case "load" -> this.load(event);
            case "list" -> this.list(event);
        }
    }

    private void create(SlashCommandInteractionEvent event) {

    }

    private void load(SlashCommandInteractionEvent event) {
    }

    private void delete(SlashCommandInteractionEvent event) {
    }

    private void list(SlashCommandInteractionEvent event) {
        User user = userService.of(event.getUser().getIdLong());

        List<Template> templates = templateService.getTemplatesOf(user.discordId());

        Function<Template, MessageEmbed.Field> templateField = t -> new MessageEmbed.Field(
          t.name(),
          "Max users: " + t.maxUsers() + "\n" +
            "Visibility: " + t.visibility() + " \n" +
            "Whitelist: " + t.whitelistUsers().size() + "\n" +
            "Blacklist: " + t.blacklistUsers().size() + "\n" +
            "Moderators: " + t.moderatorUsers().size(),
          true
        );

        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(
          event.getUser().getName(),
          null,
          event.getUser().getAvatarUrl()
        );
        builder.setTitle("Templates of `%s`".formatted(event.getUser().getName()));
        builder.setDescription("""
          **¯\\_(ツ)_/¯** *Unfortunately, it seems that you don't have any template yet.*
                    
          **You can create one by using the command `/template create <name>`.**
          """.stripIndent());
        builder.setColor(0x00FF00);
        builder.setImage(null);

        if (templates.size() != 0) builder.setDescription("");
        templates.forEach(t -> builder.addField(templateField.apply(t)));

        event.replyEmbeds(builder.build()).queue();
    }

    @Override
    public void complete(CommandAutoCompleteInteractionEvent event) {
        if (event.getSubcommandName().equalsIgnoreCase("delete")
          || event.getSubcommandName().equalsIgnoreCase("load")) {
            event
              .replyChoices(templateService.completeTemplatesOf(event.getUser().getIdLong()))
              .queue();
        }
    }
}
