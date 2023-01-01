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

import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_VOICE_STATES;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@Slf4j
public class BotConfiguration {

    @Bean
    ConfigModel config() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Path configuration = Path.of("config", "config.json");

        if (!Files.exists(configuration)) {
            Files.createDirectories(configuration.getParent());
            Files.createFile(configuration);

            ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();
            ConfigModel model = new ConfigModel(
                    "TOKEN HERE", "postgresql://localhost", 5432, "postgres", "postgres", "privaterooms");

            BufferedWriter fileWriter = Files.newBufferedWriter(configuration);
            objectWriter.writeValue(fileWriter, model);

            fileWriter.close();

            log.error("Please fill the config file and restart the bot");
            System.exit(1);
            return null;
        }

        ObjectReader objectReader = mapper.readerFor(ConfigModel.class);
        BufferedReader fileReader = Files.newBufferedReader(configuration);

        ConfigModel model = objectReader.readValue(fileReader);
        fileReader.close();

        return model;
    }

    @Lazy
    @Bean
    JDA bot(ConfigModel model) {
        return JDABuilder.create(model.token(), GUILD_VOICE_STATES).build();
    }

    public record ConfigModel(
            String token, String dbHost, int dbPort, String dbUser, String dbPassword, String dbName) {}
}
