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

package org.gravendev.privaterooms.configuration;

import fluent.bundle.FluentBundle;
import fluent.bundle.FluentResource;
import fluent.functions.cldr.CLDRFunctionFactory;
import fluent.syntax.parser.FTLParser;
import fluent.syntax.parser.FTLStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.gravendev.privaterooms.i18n.LanguageMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LanguageConfiguration {

    @Bean
    Path[] languageFiles() throws IOException {
        Path container = Path.of("languages");

        if (!Files.exists(container)) {
            Files.createDirectory(container);
        }

        try (Stream<Path> s = Files.list(container)) {
            return s.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".ftl"))
                    .toArray(Path[]::new);
        }
    }

    @Bean
    LanguageMap languageBundles(Path[] languageFiles) throws IOException {
        LanguageMap bundles = new LanguageMap(DiscordLocale.class);

        log.info("Loading languages...");
        for (Path path : languageFiles) {
            FluentResource resource = FTLParser.parse(FTLStream.of(Files.readString(path)));

            String languageName = path.getFileName().toString().split("\\.")[0];
            Locale locale = Locale.forLanguageTag(languageName);
            if (locale == null) {
                log.warn("Unable to figure what is the language of the file {}, skipping", path);
                continue;
            }

            FluentBundle bundle = FluentBundle.builder(locale, CLDRFunctionFactory.INSTANCE)
                    .addResource(resource)
                    .build();

            log.info("Loaded file {} for locale {}", path, locale.getDisplayLanguage(Locale.ENGLISH));
            bundles.put(DiscordLocale.from(locale), bundle);
        }
        log.info("Language loading finished! Found {} language(s).", bundles.size());

        return bundles;
    }

    @Bean
    Locale defaultLocale() {
        return Locale.ENGLISH;
    }

    @Bean
    DiscordLocale defaultDiscordLocale() {
        return DiscordLocale.ENGLISH_US;
    }
}
