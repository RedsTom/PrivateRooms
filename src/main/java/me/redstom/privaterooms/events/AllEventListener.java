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
import me.redstom.privaterooms.util.MigrationManager;
import me.redstom.privaterooms.util.command.ICommand;
import me.redstom.privaterooms.util.events.RegisterListener;
import me.redstom.privaterooms.util.i18n.I18n;
import me.redstom.privaterooms.util.i18n.Translator;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

@RegisterListener
@RequiredArgsConstructor
@Slf4j
public class AllEventListener extends ListenerAdapter {

    private final List<ICommand> commands;
    private final MigrationManager migrationManager;
    private final I18n i18n;

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
        this.commands.stream()
          .filter(cmd -> event.getCommandPath().startsWith(cmd.command().getName()))
          .forEach(cmd -> cmd.execute(event));
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        this.commands.stream()
          .filter(cmd -> event.getCommandPath().startsWith(cmd.command().getName()))
          .forEach(cmd -> cmd.complete(event));
    }
}
