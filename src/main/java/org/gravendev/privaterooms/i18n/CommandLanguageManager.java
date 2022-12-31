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

package org.gravendev.privaterooms.i18n;

import fluent.bundle.FluentBundle;
import java.util.EnumMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandLanguageManager {

    private final DiscordLocale                        defaultDiscordLocale;
    private final EnumMap<DiscordLocale, FluentBundle> bundles;

    public SlashCommandData adapt(SlashCommandData data) {
        CommandDataAdapter adapter = CommandDataAdapter.builder()
                .setName(data::setName)
                .getName(data::getName)
                .setDescription(data::setDescription)
                .getDescription(data::getDescription)
                .setNameLocalization(data::setNameLocalization)
                .setDescriptionLocalization(data::setDescriptionLocalization)
                .build();

        applyTo(adapter);

        data.getSubcommandGroups().forEach(this::adapt);
        data.getSubcommands().forEach(this::adapt);

        return data;
    }

    public SubcommandGroupData adapt(SubcommandGroupData data) {
        CommandDataAdapter adapter = CommandDataAdapter.builder()
                .setName(data::setName)
                .getName(data::getName)
                .setDescription(data::setDescription)
                .getDescription(data::getDescription)
                .setNameLocalization(data::setNameLocalization)
                .setDescriptionLocalization(data::setDescriptionLocalization)
                .build();

        applyTo(adapter);

        data.getSubcommands().forEach(this::adapt);

        return data;
    }

    public SubcommandData adapt(SubcommandData data) {
        CommandDataAdapter adapter = CommandDataAdapter.builder()
                .setName(data::setName)
                .getName(data::getName)
                .setDescription(data::setDescription)
                .getDescription(data::getDescription)
                .setNameLocalization(data::setNameLocalization)
                .setDescriptionLocalization(data::setDescriptionLocalization)
                .build();

        applyTo(adapter);

        return data;
    }

    private void applyTo(CommandDataAdapter data) {
        String name = data.getName().get();
        String description = data.getDescription().get();

        FluentBundle defaultBundle = bundles.get(defaultDiscordLocale);
        data.setName().accept(defaultBundle.format(name));
        data.setDescription().accept(defaultBundle.format(description));

        bundles.forEach((locale, bundle) -> {
            if (locale == defaultDiscordLocale) {
                return;
            }

            data.setNameLocalization().accept(locale, bundle.format(name));
            data.setDescriptionLocalization().accept(locale, bundle.format(description));
        });
    }

    @Builder
    @Data
    private static class CommandDataAdapter {

        private Supplier<String> getName;
        private Supplier<String> getDescription;
        private Consumer<String> setName;
        private Consumer<String> setDescription;
        private BiConsumer<DiscordLocale, String> setNameLocalization;
        private BiConsumer<DiscordLocale, String> setDescriptionLocalization;

    }
}
