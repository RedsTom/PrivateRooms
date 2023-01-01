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
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Getter
@Setter
public class TranslatableOptionData extends OptionData implements ITranslatableCommmandData<OptionData> {

    private Map<String, Object> args;
    private String nameKey, descriptionKey;

    public TranslatableOptionData(OptionType type, String nameKey, String descriptionKey) {
        this(type, nameKey, descriptionKey, false);
    }

    public TranslatableOptionData(OptionType type, String nameKey, String descriptionKey, boolean isRequired) {
        this(type, nameKey, descriptionKey, isRequired, false);
    }

    public TranslatableOptionData(
            OptionType type, String nameKey, String descriptionKey, boolean isRequired, boolean isAutoComplete) {
        super(type, CommandUtils.randomCommandName(), CommandUtils.randomCommandName(), isRequired, isAutoComplete);
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
    }
}
