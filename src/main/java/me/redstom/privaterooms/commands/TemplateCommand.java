/*
 * PrivateRooms is a discord bot to manage vocal chats.
 * Copyright (C) 2022 GravenDev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.redstom.privaterooms.commands;

import lombok.RequiredArgsConstructor;
import me.redstom.privaterooms.entities.entity.Guild;
import me.redstom.privaterooms.entities.entity.Template;
import me.redstom.privaterooms.entities.entity.User;
import me.redstom.privaterooms.entities.services.GuildService;
import me.redstom.privaterooms.entities.services.TemplateService;
import me.redstom.privaterooms.entities.services.UserService;
import me.redstom.privaterooms.util.command.CommandExecutor;
import me.redstom.privaterooms.util.command.ICommand;
import me.redstom.privaterooms.util.command.RegisterCommand;
import me.redstom.privaterooms.util.i18n.I18n;
import me.redstom.privaterooms.util.i18n.Translator;
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
    private final GuildService guildService;
    private final I18n i18n;

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

    @CommandExecutor("template/create")
    public void create(SlashCommandInteractionEvent event) {

    }

    @CommandExecutor("template/delete")
    public void load(SlashCommandInteractionEvent event) {
    }

    @CommandExecutor("template/load")
    public void delete(SlashCommandInteractionEvent event) {
    }

    @CommandExecutor("template/list")
    public void list(SlashCommandInteractionEvent event) {
        User user = userService.of(event.getUser().getIdLong());
        Guild guild = guildService.of(event.getGuild().getIdLong());

        Translator translator = i18n.of(guild.locale());

        List<Template> templates = templateService.getTemplatesOf(user.discordId());

        Function<Template, MessageEmbed.Field> templateField = t -> new MessageEmbed.Field(
          "`%s` :".formatted(t.name()),
          translator.get("commands.template.description")
            .with("max_users", t.model().maxUsers())
            .with("visibility", t.model().visibility())
            .with("whitelist_user", t.model().whitelistUsers().size())
            .with("whitelist_role", t.model().whitelistRoles().size())
            .with("blacklist_user", t.model().blacklistUsers().size())
            .with("blacklist_role", t.model().blacklistRoles().size())
            .with("moderator_user", t.model().moderatorUsers().size())
            .with("moderator_role", t.model().moderatorRoles().size())
            .toString(),
          true
        );

        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(
          event.getUser().getName(),
          null,
          event.getUser().getAvatarUrl()
        );
        builder.setTitle(translator.get("commands.template.list.title")
          .with("username", event.getUser().getName())
          .toString()
        );
        builder.setDescription(translator.raw("commands.template.list.no-templates"));
        builder.setColor(0x00FF00);

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
