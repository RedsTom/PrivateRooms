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

package me.redstom.privaterooms.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.redstom.privaterooms.entities.entity.Guild;
import me.redstom.privaterooms.entities.services.GuildService;
import me.redstom.privaterooms.entities.services.RoomService;
import me.redstom.privaterooms.util.MigrationManager;
import me.redstom.privaterooms.util.command.CommandExecutorRepr;
import me.redstom.privaterooms.util.command.ICommand;
import me.redstom.privaterooms.util.events.RegisterListener;
import me.redstom.privaterooms.util.i18n.I18n;
import me.redstom.privaterooms.util.i18n.Translator;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@RegisterListener
@RequiredArgsConstructor
@Slf4j
public class AllEventListener extends ListenerAdapter {

    private final List<ICommand> commands;
    private final Map<String, CommandExecutorRepr> commandExecutors;
    private final MigrationManager migrationManager;
    private final I18n i18n;
    private final GuildService guildService;
    private final RoomService roomService;
    private final ApplicationContext ctx;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Translator locale = i18n.of(Locale.getDefault());

        log.info(locale.get("ready")
          .with("username", event.getJDA().getSelfUser().getAsTag())
          .toString()
        );


        if (Files.exists(Path.of("config.yml"))) {
            migrationManager.run();
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Optional
          .ofNullable(this.commandExecutors.get(event.getCommandPath()))
          .ifPresent(repr -> repr.run(event));
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        this.commands.stream()
          .filter(cmd -> event.getCommandPath().startsWith(cmd.command().getName()))
          .forEach(cmd -> cmd.complete(event));
    }

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        Guild guild = guildService.rawOf(event.getGuild());
        if (event.getChannelJoined().getIdLong() == guild.createChannelId()) {
            roomService.create(guild, event.getMember());
        }
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        if (event.getChannelLeft().getType() != ChannelType.VOICE) return;
        if (event.getChannelLeft().getMembers().size() != 0) return;

        Guild guild = guildService.rawOf(event.getGuild());
        VoiceChannel voiceChannel = (VoiceChannel) event.getChannelLeft();

        if (voiceChannel.getParentCategory().getIdLong() == guild.categoryId()) {
            roomService.of(event.getChannelLeft().getIdLong()).ifPresent(roomService::delete);
        }
    }
}
