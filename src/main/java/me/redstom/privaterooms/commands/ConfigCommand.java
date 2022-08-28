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
import me.redstom.privaterooms.entities.entity.Room;
import me.redstom.privaterooms.entities.entity.Template;
import me.redstom.privaterooms.entities.entity.User;
import me.redstom.privaterooms.entities.services.GuildService;
import me.redstom.privaterooms.entities.services.RoomService;
import me.redstom.privaterooms.entities.services.TemplateService;
import me.redstom.privaterooms.entities.services.UserService;
import me.redstom.privaterooms.util.command.ICommand;
import me.redstom.privaterooms.util.command.RegisterCommand;
import me.redstom.privaterooms.util.command.CommandExecutor;
import me.redstom.privaterooms.util.i18n.I18n;
import me.redstom.privaterooms.util.i18n.Translator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.Optional;
import java.util.function.Function;

@RegisterCommand
@RequiredArgsConstructor
public class ConfigCommand implements ICommand {

    private final UserService userService;
    private final RoomService roomService;
    private final GuildService guildService;
    private final TemplateService templateService;
    private final I18n i18n;

    @Override
    public CommandData command() {
        Function<String, SubcommandGroupData> listGroup = (name) ->
          new SubcommandGroupData(name, "Manage the %s of the channel".formatted(name))
            .addSubcommands(
              new SubcommandData("add", "Add a user to the %s".formatted(name))
                .addOption(OptionType.USER, "user", "The user to add to the %s".formatted(name), true),
              new SubcommandData("remove", "Removes an user from the %s".formatted(name))
                .addOption(OptionType.USER, "user", "The user to remove from the %s".formatted(name), true),
              new SubcommandData("add-role", "Adds a role to the %s".formatted(name))
                .addOption(OptionType.ROLE, "role", "The role to add to the %s".formatted(name), true),
              new SubcommandData("remove-role", "Removes a role from the %s".formatted(name))
                .addOption(OptionType.ROLE, "role", "The role to remove from the %s".formatted(name), true),
              new SubcommandData("list", "List the users in the %s".formatted(name))
                .addOptions(new OptionData(OptionType.STRING, "filter", "The filter of the %s".formatted(name), false)
                  .addChoice("all", "0")
                  .addChoice("user", "1")
                  .addChoice("role", "2")
                )
            );

        return Commands.slash("config", "Configure your current channel")
          .setGuildOnly(true)
          .addSubcommands(
            new SubcommandData("name", "Rename the channel")
              .addOption(OptionType.STRING, "name", "The new name of the channel", true),
            new SubcommandData("visibility", "Changes the visibility of the channel")
              .addOptions(new OptionData(OptionType.STRING, "visibility", "The new visibility of the channel", true, false)
                .addChoice("public", "0")
                .addChoice("private", "1")
                .addChoice("hidden", "2")
              ),
            new SubcommandData("restore", "Restores the previous channel configuration")
          ).addSubcommandGroups(
            listGroup.apply("whitelist"),
            listGroup.apply("blacklist"),
            listGroup.apply("moderators")
          );
    }

    @Override
    public boolean check(SlashCommandInteractionEvent event) {
        Guild guild = guildService.of(event.getGuild().getIdLong());
        Member member = event.getMember();

        Translator translator = i18n.of(guild.locale());

        if (!member.getVoiceState().inAudioChannel()) {
            event.replyEmbeds(new EmbedBuilder()
              .setTitle(translator.raw("commands.config.error.title"))
              .setDescription(translator.raw("commands.config.error.not-in-voice"))
              .setColor(0xFF0000)
              .build()
            ).queue();

            return false;
        }

        VoiceChannel channel = (VoiceChannel) member.getVoiceState().getChannel();

        if (channel.getParentCategory().getIdLong() != guild.categoryId()) {
            event.replyEmbeds(new EmbedBuilder()
              .setTitle(translator.raw("commands.config.error.title"))
              .setDescription(translator.raw("commands.config.error.wrong-category"))
              .setColor(0xFF0000)
              .build()
            ).queue();

            return false;
        }

        if (roomService.of(channel.getIdLong()).isEmpty()) {
            event.replyEmbeds(new EmbedBuilder()
              .setTitle(translator.raw("commands.config.error.title"))
              .setDescription(translator.raw("commands.config.error.not-a-room"))
              .setColor(0xFF0000)
              .build()
            ).queue();

            return false;
        }

        return true;
    }

    @CommandExecutor("config/name")
    public void name(SlashCommandInteractionEvent event) {
        CommandContext ctx = of(event);

        roomService.update(ctx.member, ctx.room, r -> r, m -> m
          .channelName(event.getOption("name").getAsString())
        );
    }

    @CommandExecutor("config/restore")
    public void restore(SlashCommandInteractionEvent event) {
        CommandContext ctx = of(event);

        Optional<Template> template = templateService.load(ctx.user, "restore_" + ctx.user.discordId());

        if (!template.isPresent()) {
            event.replyEmbeds(new EmbedBuilder()
              .setTitle(ctx.translator.raw("commands.config.error.title"))
              .setDescription(ctx.translator.raw("commands.config.restore.error.no-template"))
              .build()
            ).queue();

            return;
        }

        roomService.update(ctx.member, ctx.room, r -> r.model(template.get().model()));
    }

    record CommandContext(
      Guild guild,
      User user,
      Member member,
      Room room,
      Translator translator
    ) {
    }

    private CommandContext of(SlashCommandInteractionEvent event) {
        Guild guild = guildService.of(event.getGuild().getIdLong());
        User user = userService.of(event.getUser().getIdLong());
        Member member = event.getMember();

        Translator translator = i18n.of(guild.locale());
        Room room = roomService.of(member.getVoiceState().getChannel().getIdLong()).get();

        return new CommandContext(guild, user, member, room, translator);
    }
}
