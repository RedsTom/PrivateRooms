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

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.redstom.privaterooms.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class I18n {

    private final Map<Locale, Translator> translators = new HashMap<>();

    public Locale[] getLocales() {
        return translators.keySet().toArray(Locale[]::new);
    }

    @Autowired
    @SneakyThrows
    public void loaded(FileUtils fileUtils) {
        File languagesFolder = new File("languages");
        if (!languagesFolder.exists()) {
            languagesFolder.mkdirs();
        }

        for (File file : languagesFolder.listFiles()) {
            log.info("Loading language file: {}", file.getName());
            register(file.getName().substring(0, file.getName().length() - 4));
        }
    }

    @SneakyThrows
    public void register(String locale) {
        Locale localeObject = Locale.forLanguageTag(locale);
        Translator translator = new Translator(localeObject);

        log.info("Registering language : {}", localeObject.getDisplayName());

        translators.put(localeObject, translator);
    }

    public Translator of(Locale locale) {
        return translators.getOrDefault(locale, translators.get(Locale.ENGLISH));
    }

}
