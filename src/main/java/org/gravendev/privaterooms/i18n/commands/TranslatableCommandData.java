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

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Getter
@Setter
public class TranslatableCommandData extends CommandDataImpl implements SlashCommandData, ITranslatableCommmandData<CommandDataImpl> {

    private Map<String, Object> args;
    private String nameKey, descriptionKey;

    public TranslatableCommandData(String nameKey, String descriptionKey) {
        super(CommandUtils.randomCommandName(), CommandUtils.randomCommandName());
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
    }

    public TranslatableCommandData(Command.Type type) {
        super(type, CommandUtils.randomCommandName());
    }

    @NotNull
    @Override
    public CommandDataImpl addOption(@NotNull OptionType type, @NotNull String name, @NotNull String description, boolean required, boolean autoComplete) {
        return addOptions(new TranslatableOptionData(type, name, description)
                .setRequired(required)
                .setAutoComplete(autoComplete));
    }

    @NotNull
    @Override
    public CommandDataImpl addOption(@NotNull OptionType type, @NotNull String name, @NotNull String description, boolean required) {
        return addOption(type, name, description, required, false);
    }

    @NotNull
    @Override
    public CommandDataImpl addOption(@NotNull OptionType type, @NotNull String name, @NotNull String description) {
        return addOption(type, name, description, false);
    }

    @Override
    public String toString() {
        return "Command[name=" + getName() + "]";
    }
}
