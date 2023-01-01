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

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class TranslatableSubcommandData extends SubcommandData implements ITranslatableCommmandData<SubcommandData> {

    private Map<String, Object> args;
    private String nameKey, descriptionKey;

    public TranslatableSubcommandData(String nameKey, String descriptionKey) {
        super(CommandUtils.randomCommandName(), CommandUtils.randomCommandName());
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
    }

    @NotNull @Override
    public SubcommandData addOption(
            @NotNull OptionType type,
            @NotNull String name,
            @NotNull String description,
            boolean required,
            boolean autoComplete) {
        return addOptions(new TranslatableOptionData(type, name, description)
                .setRequired(required)
                .setAutoComplete(autoComplete));
    }

    @NotNull @Override
    public SubcommandData addOption(
            @NotNull OptionType type, @NotNull String name, @NotNull String description, boolean required) {
        return addOption(type, name, description, required, false);
    }

    @NotNull @Override
    public SubcommandData addOption(@NotNull OptionType type, @NotNull String name, @NotNull String description) {
        return addOption(type, name, description, false);
    }

    public SubcommandData addOption(
            CommandLanguageManager clm,
            OptionType type,
            String name,
            String description,
            boolean required,
            boolean autoComplete) {
        return addOptions(clm.adapt(new TranslatableOptionData(type, name, description)
                .setRequired(required)
                .setAutoComplete(autoComplete)));
    }

    public SubcommandData addOption(
            CommandLanguageManager clm, OptionType type, String name, String description, boolean required) {
        return this.addOption(clm, type, name, description, required, false);
    }

    public SubcommandData addOption(CommandLanguageManager clm, OptionType type, String name, String description) {
        return this.addOption(clm, type, name, description, false);
    }
}
