/*
 * PrivateRooms is a bot managing vocal channels in a server
 * Copyright (C) 2022 RedsTom
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

package org.gravendev.privaterooms.listeners;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.gravendev.privaterooms.commands.utils.CommandContainer;
import org.gravendev.privaterooms.commands.utils.CommandDeclaration;
import org.gravendev.privaterooms.commands.utils.CommandExecutor;
import org.gravendev.privaterooms.commands.utils.CommandExecutorRepr;
import org.gravendev.privaterooms.i18n.commands.CommandLanguageManager;
import org.gravendev.privaterooms.listeners.utils.Listener;
import org.springframework.context.ApplicationContext;

@Listener
@RequiredArgsConstructor
public class ReadyListener extends ListenerAdapter {

    private final ApplicationContext ctx;
    private final Map<String, CommandExecutorRepr> commands;
    private final JDA jda;
    private final CommandLanguageManager clm;

    @Override
    public void onReady(@NonNull ReadyEvent event) {
        loadCommands();
    }

    private void loadCommands() {
        List<CommandData> commandData = ctx.getBeansWithAnnotation(CommandContainer.class).values().stream()
                .flatMap(a -> Arrays.stream(a.getClass().getMethods())
                        .filter(m -> m.isAnnotationPresent(CommandDeclaration.class))
                        .map(m -> {
                            try {
                                return m.invoke(a, clm);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        })
                        .map(CommandData.class::cast))
                .toList();

        System.out.println("Command list is : " + commandData);

        this.jda
                .updateCommands()
                .addCommands(commandData.toArray(CommandData[]::new))
                .queue();

        Map<String, CommandExecutorRepr> retrievedCommands =
                ctx.getBeansWithAnnotation(CommandContainer.class).values().stream()
                        .flatMap(a -> Arrays.stream(a.getClass().getMethods()).map(m -> new CommandExecutorRepr(a, m)))
                        .filter(c -> c.method().isAnnotationPresent(CommandExecutor.class))
                        .collect(Collectors.toMap(
                                c -> c.method()
                                        .getAnnotation(CommandExecutor.class)
                                        .path(),
                                c -> c));

        this.commands.putAll(retrievedCommands);
    }
}
