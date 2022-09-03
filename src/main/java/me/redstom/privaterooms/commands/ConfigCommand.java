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
import me.redstom.privaterooms.entities.entity.ModelEntity;
import me.redstom.privaterooms.entities.entity.ModelEntityType;
import me.redstom.privaterooms.entities.entity.Template;
import me.redstom.privaterooms.entities.services.*;
import me.redstom.privaterooms.util.Colors;
import me.redstom.privaterooms.util.command.CommandExecutor;
import me.redstom.privaterooms.util.command.ICommand;
import me.redstom.privaterooms.util.command.RegisterCommand;
import me.redstom.privaterooms.util.i18n.I18n;
import me.redstom.privaterooms.util.room.RoomCommandUtils;
import me.redstom.privaterooms.util.room.RoomCommandUtils.RoomCommandContext;
import me.redstom.privaterooms.util.room.RoomVisibility;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.Optional;
import java.util.function.Function;

@RegisterCommand
@RequiredArgsConstructor
public class ConfigCommand implements ICommand {

    private final RoomCommandUtils roomUtils;
    private final RoomService roomService;
    private final RoleService roleService;
    private final UserService userService;
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
                .addChoice("public", RoomVisibility.PUBLIC.name())
                .addChoice("private", RoomVisibility.PRIVATE.name())
                .addChoice("hidden", RoomVisibility.HIDDEN.name())
              ),
            new SubcommandData("restore", "Restores the previous channel configuration"),
            new SubcommandData("limit", "Sets the user limit of the channel")
              .addOption(OptionType.INTEGER, "limit", "The new limit of the channel", true)
          ).addSubcommandGroups(
            listGroup.apply("whitelist"),
            listGroup.apply("blacklist"),
            listGroup.apply("moderators")
          );
    }

    @Override
    public boolean check(SlashCommandInteractionEvent event) {
        return roomUtils.check(event);
    }

    @CommandExecutor("config/name")
    public void name(SlashCommandInteractionEvent event) {
        RoomCommandContext ctx = roomUtils.contextOf(event);
        String name = event.getOption("name").getAsString();

        roomService.update(ctx.member(), ctx.room(), m -> m
          .channelName(name)
        );

        event.replyEmbeds(new EmbedBuilder()
          .setTitle(ctx.translator().raw("commands.config.name.title"))
          .setDescription(ctx.translator().get("commands.config.name.description")
            .with("name", name)
            .toString()
          )
          .setColor(Colors.GREEN)
          .build()
        ).queue();
    }

    @CommandExecutor("config/restore")
    public void restore(SlashCommandInteractionEvent event) {
        RoomCommandContext ctx = roomUtils.contextOf(event);

        Optional<Template> template = templateService.load(ctx.user(), "previous");

        if (!template.isPresent()) {
            event.replyEmbeds(new EmbedBuilder()
              .setTitle(ctx.translator().raw("commands.config.error.title"))
              .setDescription(ctx.translator().raw("commands.config.restore.error.no-template"))
              .build()
            ).queue();

            return;
        }

        roomService.update(ctx.member(), ctx.room(), m -> template.get().model());

        event.replyEmbeds(new EmbedBuilder()
          .setTitle(i18n.of(ctx.guild().locale()).raw("commands.config.restore.title"))
          .setDescription(ctx.translator().raw("commands.config.restore.description"))
          .setColor(Colors.GREEN)
          .build()
        ).queue();
    }

    @CommandExecutor("config/visibility")
    public void visibility(SlashCommandInteractionEvent event) {
        RoomCommandContext ctx = roomUtils.contextOf(event);
        RoomVisibility visibility = event.getOption("visibility", (s) -> RoomVisibility.valueOf(s.getAsString()));

        roomService.update(ctx.member(), ctx.room(), m -> m.visibility(visibility));

        event.replyEmbeds(new EmbedBuilder()
          .setTitle(ctx.translator().raw("commands.config.visibility.title"))
          .setDescription(ctx.translator().get("commands.config.visibility.description")
            .with("visibility", visibility.name())
            .toString()
          )
          .setColor(Colors.GREEN)
          .build()
        ).queue();
    }

    @CommandExecutor("config/limit")
    public void limit(SlashCommandInteractionEvent event) {
        RoomCommandContext ctx = roomUtils.contextOf(event);
        int limit = event.getOption("limit").getAsInt();

        roomService.update(ctx.member(), ctx.room(), m -> m.maxUsers(limit));

        event.replyEmbeds(new EmbedBuilder()
          .setTitle(ctx.translator().raw("commands.config.limit.title"))
          .setDescription(ctx.translator().get("commands.config.limit.description")
            .with("limit", limit)
            .toString()
          )
          .setColor(Colors.GREEN)
          .build()
        ).queue();
    }

    private void addUserTo(SlashCommandInteractionEvent event, ModelEntityType type, String commandKey) {
        RoomCommandContext ctx = roomUtils.contextOf(event);
        User user = event.getOption("user").getAsUser();

        me.redstom.privaterooms.entities.entity.User u = userService.of(user.getIdLong());

        roomService.update(ctx.member(), ctx.room(), m -> {
              m.users().add(ModelEntity.ModelUser.builder()
                .referringUser(u)
                .type(type)
                .build()
              );
              return m;
          }
        );

        event.replyEmbeds(new EmbedBuilder()
          .setTitle(ctx.translator().raw("commands.config." + commandKey + ".add.title"))
          .setDescription(ctx.translator().get("commands.config." + commandKey + ".add.description")
            .with("user", user.getAsMention())
            .toString()
          )
          .setColor(Colors.GREEN)
          .build()
        ).queue();
    }

    @CommandExecutor("config/whitelist/add")
    public void addToWhitelist(SlashCommandInteractionEvent event) {
        addUserTo(event, ModelEntityType.WHITELIST, "whitelist");
    }

    @CommandExecutor("config/blacklist/add")
    public void addToBlacklist(SlashCommandInteractionEvent event) {
        addUserTo(event, ModelEntityType.BLACKLIST, "blacklist");
    }

    @CommandExecutor("config/moderators/add")
    public void addToModerators(SlashCommandInteractionEvent event) {
        addUserTo(event, ModelEntityType.MODERATOR, "moderators");
    }
}
