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
import me.redstom.privaterooms.db.entity.Guild;
import me.redstom.privaterooms.db.services.GuildService;
import me.redstom.privaterooms.util.command.ICommand;
import me.redstom.privaterooms.util.command.RegisterCommand;
import me.redstom.privaterooms.util.i18n.I18n;
import me.redstom.privaterooms.util.i18n.Translator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.Arrays;
import java.util.Locale;

@RegisterCommand
@RequiredArgsConstructor
public class SettingsCommand implements ICommand {

    private final I18n i18n;
    private final GuildService guildService;

    @Override
    public CommandData command() {
        return Commands.slash("settings", "Configure the bot settings for the current serveur")
          .setGuildOnly(true)
          .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
          .addSubcommands(
            new SubcommandData("locale", "Sets the locale of the bot for this server")
              .addOption(OptionType.STRING, "locale", "The locale of the bot", true, true)
          )
          .addSubcommandGroups(
            new SubcommandGroupData("channels", "Configure the channels")
              .addSubcommands(
                new SubcommandData("new", "Set the \"Create room\" channel")
                  .addOption(OptionType.CHANNEL, "channel", "The channel to set", true),
                new SubcommandData("category", "Sets the category where rooms are created")
                  .addOption(OptionType.CHANNEL, "channel", "The channel to set", true)
              )
          );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        switch (event.getCommandPath()) {
            case "settings/locale" -> this.locale(event);
        }
    }

    private void locale(SlashCommandInteractionEvent event) {
        Guild guild = guildService.rawOf(event.getGuild().getIdLong());

        Locale locale = event.getOption("locale", s -> Locale.forLanguageTag(s.getAsString()));
        guildService.update(guild, g -> g.locale(locale));

        Translator translator = i18n.of(locale);
        event.replyEmbeds(new EmbedBuilder()
          .setColor(0x00FF00)
          .setDescription(translator.get("commands.settings.locale.success")
            .with("locale", locale.getDisplayName(locale))
            .toString()
          )
          .build()
        ).queue();
    }

    @Override
    public void complete(CommandAutoCompleteInteractionEvent event) {
        if (event.getCommandPath().equalsIgnoreCase("settings/locale")) {
            event.replyChoices(Arrays.stream(i18n.getLocales())
              .map(locale -> new Command.Choice(locale.getDisplayName(locale), locale.toLanguageTag()))
              .toArray(Command.Choice[]::new)
            ).queue();
        }
    }
}
