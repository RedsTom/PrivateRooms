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

package org.gravendev.privaterooms.i18n.keys;

import fluent.bundle.FluentBundle;
import java.util.Map;

public interface TranslationKeys {

    String key();

    default String format(FluentBundle bundle) {
        return bundle.format(key());
    }

    default String format(FluentBundle bundle, Map<String, ?> args) {
        return bundle.format(key(), args);
    }
}
