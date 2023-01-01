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

package org.gravendev.privaterooms.i18n.commands;

import fluent.bundle.FluentBundle;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.gravendev.privaterooms.i18n.LanguageMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component

@AllArgsConstructor
@Slf4j
public class CommandLanguageManager {

    private Map<String, Object> args;
    private final DiscordLocale defaultDiscordLocale;
    private final LanguageMap bundles;

    @Autowired
    public CommandLanguageManager(DiscordLocale defaultDiscordLocale, LanguageMap bundles) {
        this.defaultDiscordLocale = defaultDiscordLocale;
        this.bundles = bundles;
    }

    public CommandLanguageManager withArgs(Map<String, Object> args) {
        return new CommandLanguageManager(args, defaultDiscordLocale, bundles);
    }

    public TranslatableCommandData adapt(SlashCommandData data) {
        if (data instanceof TranslatableCommandData d) {
            return adapt(d);
        }

        return null;
    }

    public TranslatableCommandData[] adapt(SlashCommandData... data) {
        TranslatableCommandData[] rt = new TranslatableCommandData[data.length];
        for (int i = 0; i < data.length; i++) {
            rt[i] = adapt(data[i]);
        }

        return rt;
    }

    public TranslatableSubcommandGroupData adapt(SubcommandGroupData data) {
        if (data instanceof TranslatableSubcommandGroupData d) {
            return adapt(d);
        }

        return null;
    }

    public TranslatableSubcommandGroupData[] adapt(SubcommandGroupData... data) {
        TranslatableSubcommandGroupData[] rt = new TranslatableSubcommandGroupData[data.length];
        for (int i = 0; i < data.length; i++) {
            rt[i] = adapt(data[i]);
        }

        return rt;
    }

    public TranslatableSubcommandData adapt(SubcommandData data) {
        if (data instanceof TranslatableSubcommandData d) {
            return adapt(d);
        }

        return null;
    }

    public TranslatableSubcommandData[] adapt(SubcommandData... data) {
        TranslatableSubcommandData[] rt = new TranslatableSubcommandData[data.length];
        for (int i = 0; i < data.length; i++) {
            rt[i] = adapt(data[i]);
        }

        return rt;
    }


    public TranslatableOptionData adapt(OptionData data) {
        if (data instanceof TranslatableOptionData d) {
            return adapt(d);
        }

        return null;
    }

    public TranslatableOptionData[] adapt(OptionData... data) {
        TranslatableOptionData[] rt = new TranslatableOptionData[data.length];
        for (int i = 0; i < data.length; i++) {
            rt[i] = adapt(data[i]);
        }

        return rt;
    }


    private TranslatableCommandData adapt(TranslatableCommandData data) {
        applyTo(data);

        data.getSubcommandGroups().stream()
                .filter(TranslatableSubcommandGroupData.class::isInstance)
                .map(TranslatableSubcommandGroupData.class::cast)
                .forEach(this::adapt);

        data.getSubcommands().stream()
                .filter(TranslatableSubcommandData.class::isInstance)
                .map(TranslatableSubcommandData.class::cast)
                .forEach(this::adapt);

        data.getOptions().stream()
                .filter(TranslatableOptionData.class::isInstance)
                .map(TranslatableOptionData.class::cast)
                .forEach(this::adapt);

        return data;
    }

    private TranslatableSubcommandGroupData adapt(TranslatableSubcommandGroupData data) {
        applyTo(data);
        data.getSubcommands().stream()
                .filter(TranslatableSubcommandData.class::isInstance)
                .map(TranslatableSubcommandData.class::cast)
                .forEach(this::adapt);

        return data;
    }

    private TranslatableSubcommandData adapt(TranslatableSubcommandData data) {
        applyTo(data);

        data.getOptions().stream()
                .filter(TranslatableOptionData.class::isInstance)
                .map(TranslatableOptionData.class::cast)
                .forEach(this::adapt);

        return data;
    }

    private TranslatableOptionData adapt(TranslatableOptionData data) {
        applyTo(data);
        return data;
    }

    private void applyTo(ITranslatableCommmandData<?> data) {
        String name = data.nameKey();
        String description = data.descriptionKey();

        FluentBundle defaultBundle = bundles.get(defaultDiscordLocale);

        if (data.args() == null) {
            data.args(new HashMap<>());
        }
        if (this.args != null) {
            data.args().putAll(this.args);
        }

        data.setName(defaultBundle.format(name, data.args()));
        data.setDescription(defaultBundle.format(description, data.args()));

        bundles.forEach(
                (locale, bundle) -> {
                    data.setNameLocalization(locale, bundle.format(name, data.args()));
                    data.setDescriptionLocalization(locale, bundle.format(description, data.args()));
                });
    }
}
