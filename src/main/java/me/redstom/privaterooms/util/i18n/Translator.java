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

package me.redstom.privaterooms.util.i18n;

import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Slf4j
public class Translator {

    private final Locale locale;
    private File file;
    private YamlConfiguration config;

    @Builder
    @SneakyThrows
    public Translator(Locale locale) {
        this.locale = locale;
        this.file = new File("languages/", "%s.yml".formatted(locale.toLanguageTag()));

        if (!file.exists()) {
            log.warn("Language file not found: {} ! Using english instead", file.getName());
            this.file = new File("languages/", "en.yml");
        }

        this.config = new YamlConfiguration();
        this.config.load(file);
    }

    public String raw(String key) {
        return config.getString(key, key);
    }

    public Message get(String key) {
        return new Message(config.getString(key, key));
    }

    public class Message {

        private static final String FILTER_PATTERN = "\\$\\{ %s \\| [a-zA-Z\\.]+ }";
        private static final String VARIABLE_PATTERN = "\\$%s";

        private String message;

        public Message(String message) {
            this.message = message;
        }

        public Message with(String key, Object value) {
            Matcher filter = Pattern.compile(FILTER_PATTERN.formatted(key)).matcher(message);

            while (filter.find()) {
                String slice = filter.group();

                String[] parts = slice.split("\\|");
                String filterName = parts[1];
                filterName = filterName.substring(0, filterName.length() - 1).trim();

                String path = "%s.%s".formatted(filterName, value);

                message = message.replace(slice, config.getString(path, path));
            }


            Matcher variable = Pattern.compile(VARIABLE_PATTERN.formatted(key)).matcher(message);

            while (variable.find()) {
                String slice = variable.group();
                message = message.replace(slice, value.toString());
            }

            return this;
        }

        public String toString() {
            return message;
        }
    }
}
