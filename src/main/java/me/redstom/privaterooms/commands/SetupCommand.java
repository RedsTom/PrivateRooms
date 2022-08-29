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
import me.redstom.privaterooms.entities.services.GuildService;
import me.redstom.privaterooms.util.Colors;
import me.redstom.privaterooms.util.command.ICommand;
import me.redstom.privaterooms.util.command.RegisterCommand;
import me.redstom.privaterooms.util.command.CommandExecutor;
import me.redstom.privaterooms.util.i18n.I18n;
import me.redstom.privaterooms.util.i18n.Translator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@RegisterCommand
@RequiredArgsConstructor
public class SetupCommand implements ICommand {

    private final GuildService guildService;
    private final I18n i18n;

    @Override
    public CommandData command() {
        return Commands
          .slash("setup", "Sets up the category and the channel to create the channels into.")
          .setGuildOnly(true)
          .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @CommandExecutor("setup")
    public void run(SlashCommandInteractionEvent event) {
        ReplyCallbackAction reply = event.deferReply();

        Guild guild = guildService.of(event.getGuild().getIdLong());
        Translator translator = i18n.of(guild.locale());

        AtomicBoolean deleted = new AtomicBoolean(false);
        if (guild.categoryId() != 0 || guild.createChannelId() != 0) {
            Optional.ofNullable(event.getGuild().getCategoryById(guild.categoryId()))
              .ifPresent(cat -> {
                  cat.getChannels().forEach(chan -> chan.delete().queue());
                  cat.delete().queue((a) -> {}, (a) -> {});

                  deleted.set(true);
              });

            Optional.ofNullable(event.getGuild().getGuildChannelById(guild.createChannelId()))
              .ifPresent(chan -> {
                  chan.delete().queue((a) -> {}, (a) -> {});

                  deleted.set(true);
              });
        }

        event.getGuild().createCategory("Private Rooms").queue(cat -> cat
          .createVoiceChannel("🔐 Create a room")
          .setUserlimit(1)
          .queue(chan -> {
              guildService.update(guild, g -> g
                .categoryId(cat.getIdLong())
                .createChannelId(chan.getIdLong())
              );

              reply.setEphemeral(true).setEmbeds(new EmbedBuilder()
                .setTitle(translator.raw("commands.setup.success.title"))
                .setDescription(translator.get("commands.setup.success.description")
                  .with("channel", chan.getAsMention())
                  .toString()
                )
                .setFooter(deleted.get() ? translator.raw("commands.setup.success.note") : "")
                .setColor(Colors.GREEN)
                .build()
              ).queue();
          }, err -> error(translator, reply, err)), err -> error(translator, reply, err));
    }

    private void error(Translator translator, ReplyCallbackAction reply, Throwable err) {

        reply.setEphemeral(true)
          .setEmbeds(new EmbedBuilder()
            .setTitle(translator.raw("commands.setup.error.title"))
            .setDescription(translator.get("commands.setup.error.description")
              .with("error", err.getMessage())
              .toString()
            )
            .setColor(0xFF0000)
            .build()
          ).queue();
    }
}
