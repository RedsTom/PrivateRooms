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

package org.gravendev.privaterooms.commands.config;

import static org.gravendev.privaterooms.commands.config.ConfigDeclaration.ConfigDeclarationKeys.*;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.gravendev.privaterooms.commands.utils.CommandContainer;
import org.gravendev.privaterooms.commands.utils.CommandDeclaration;
import org.gravendev.privaterooms.i18n.commands.CommandLanguageManager;
import org.gravendev.privaterooms.i18n.commands.TranslatableCommandData;
import org.gravendev.privaterooms.i18n.commands.TranslatableSubcommandData;
import org.gravendev.privaterooms.i18n.commands.TranslatableSubcommandGroupData;
import org.gravendev.privaterooms.i18n.keys.TranslationKeys;

@CommandContainer
@RequiredArgsConstructor
public class ConfigDeclaration {

    private final CommandLanguageManager clm;

    @CommandDeclaration
    public CommandData config(CommandLanguageManager clm) {
        return clm.adapt(new TranslatableCommandData(COMMAND_NAME.key(), COMMAND_DESCRIPTION.key())
                .addSubcommandGroups(configGroup("whitelist"), configGroup("blacklist"), configGroup("moderators"))
                .addSubcommands());
    }

    private SubcommandGroupData configGroup(String name) {
        CommandLanguageManager adapted = clm.withArgs(Map.of("name", name));
        return adapted.adapt(new TranslatableSubcommandGroupData(GROUP_NAME.key, GROUP_DESCRIPTION.key)
                .addSubcommands(adapted.adapt(
                        new TranslatableSubcommandData(GROUP_ADD_NAME.key, GROUP_ADD_DESCRIPTION.key)
                                .addOption(adapted, OptionType.USER, USER.key, "\u200E", true),
                        new TranslatableSubcommandData(GROUP_REMOVE_NAME.key, GROUP_REMOVE_DESCRIPTION.key)
                                .addOption(adapted, OptionType.USER, USER.key, "\u200E", true),
                        new TranslatableSubcommandData(GROUP_LIST_NAME.key, GROUP_LIST_DESCRIPTION.key),
                        new TranslatableSubcommandData(GROUP_ADD_ROLE_NAME.key, GROUP_ADD_ROLE_DESCRIPTION.key)
                                .addOption(adapted, OptionType.ROLE, ROLE.key, "\u200E", true),
                        new TranslatableSubcommandData(GROUP_REMOVE_ROLE_NAME.key, GROUP_REMOVE_ROLE_DESCRIPTION.key)
                                .addOption(adapted, OptionType.ROLE, ROLE.key, "\u200E", true))));
    }

    @RequiredArgsConstructor
    @Getter
    enum ConfigDeclarationKeys implements TranslationKeys {
        COMMAND_NAME("commands-config-name"),
        COMMAND_DESCRIPTION("commands-config-description"),

        GROUP_NAME("commands-config-group-name"),
        GROUP_DESCRIPTION("commands-config-group-description"),
        GROUP_ADD_NAME("commands-config-group-add-name"),
        GROUP_ADD_DESCRIPTION("commands-config-group-add-description"),
        GROUP_ADD_ROLE_NAME("commands-config-group-add-role-name"),
        GROUP_ADD_ROLE_DESCRIPTION("commands-config-group-add-role-description"),
        GROUP_REMOVE_NAME("commands-config-group-remove-name"),
        GROUP_REMOVE_DESCRIPTION("commands-config-group-remove-description"),
        GROUP_REMOVE_ROLE_NAME("commands-config-group-remove-role-name"),
        GROUP_REMOVE_ROLE_DESCRIPTION("commands-config-group-remove-role-description"),
        GROUP_LIST_NAME("commands-config-group-list-name"),
        GROUP_LIST_DESCRIPTION("commands-config-group-list-description"),

        USER("commands-config-user"),
        ROLE("commands-config-role");
        private final String key;
    }
}
