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

package me.redstom.privaterooms.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

public class ActionMapper<T> {

    public static <T> ActionMapper<T> of(T value) {
        return new ActionMapper<>(value);
    }

    private T value;
    private List<String> actions;

    private ActionMapper(T value) {
        this.value = value;
        this.actions = new ArrayList<>();
    }

    public ActionMapper<T> map(UnaryOperator<T> action, String reason) {
        if(this.value != null) {
            this.value = action.apply(this.value);
            this.actions.add(reason);
        }
        return this;
    }

    public ActionMapper<T> then(BiConsumer<T, String[]> action) {
        if(this.value != null) {
            action.accept(this.value, this.actions.toArray(String[]::new));
        }
        return this;
    }
}
