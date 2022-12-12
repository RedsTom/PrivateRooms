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

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.moandjiezana.toml.Toml;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.redstom.privaterooms.entities.entity.Guild;
import me.redstom.privaterooms.entities.entity.Template;
import me.redstom.privaterooms.entities.entity.User;
import me.redstom.privaterooms.entities.entity.model.Model;
import me.redstom.privaterooms.entities.entity.model.ModelEntityType;
import me.redstom.privaterooms.entities.entity.model.ModelRole;
import me.redstom.privaterooms.entities.entity.model.ModelUser;
import me.redstom.privaterooms.entities.services.GuildService;
import me.redstom.privaterooms.entities.services.RoleService;
import me.redstom.privaterooms.entities.services.TemplateService;
import me.redstom.privaterooms.entities.services.UserService;
import me.redstom.privaterooms.util.room.RoomVisibility;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MigrationManager {

    private final UserService     userService;
    private final RoleService     roleService;
    private final GuildService    guildService;
    private final TemplateService templateService;

    @SneakyThrows
    public void run() {
        Lists.newArrayList("""
                                   --------------------------------------------------------------
                                   Detected old private rooms config and saves !
                                   Those are not supported anymore ! Starting migration...
                                             
                                   Please note that the old configuration will not be migrated !
                                   The migrated elements will be :
                                    - The templates
                                    - The server configurations
                                   --------------------------------------------------------------
                                   """.split("\n")
        ).forEach(log::info);

        this.migrateServers();
        this.migrateTemplates();

        log.info("Migration finished ! All the data from the files are now in the database !");
        BufferedReader reader = new BufferedReader(new FileReader("config.yml"));
        log.info("Here's your old config.yml : \n{}", reader
                .lines()
                .collect(Collectors.joining("\n"))
        );
        reader.close();
        log.info("The config.yml file will be deleted to prevent any further migration.");
        try {
            Files.deleteIfExists(Path.of("config.yml"));
            log.info("The config.yml file has been deleted.");
        } catch (Exception e) {
            log.warn("The config.yml file could not be deleted. Please delete it manually!");
        }
        log.info("------------------------------------------------------");
    }

    private void migrateServers() {
        log.info("Migrating servers...");

        File serverFolder = new File("servers");

        if (!serverFolder.exists()) {
            log.info("No servers found, skipping migration...");
            return;
        }

        for (File file : serverFolder.listFiles()) {
            Toml toml = new Toml().read(file);

            long serverId = Long.parseLong(file.getName().replace(".toml", ""));
            String categoryId = toml.getString("categoryId");
            String createChannelId = toml.getString("createChannelId", "0");

            if (categoryId.isEmpty() || createChannelId.isEmpty()) {
                log.debug("Skipping server " + serverId + " because it is not fully configured...");
                continue;
            }

            guildService.update(serverId, g -> g
                    .categoryId(Long.parseLong(categoryId))
                    .createChannelId(Long.parseLong(createChannelId))
            );

            log.debug("Migrated server " + serverId);
        }

        log.info("Servers migration finished !");
        log.info("--------------------------------------------------------------");
    }

    private void migrateTemplates() {
        log.info("Migrating user templates...");
        File templateFolder = new File("templates");

        if (!templateFolder.exists()) {
            log.info("No templates found, skipping migration...");
            return;
        }

        for (File file : templateFolder.listFiles()) {
            if (!file.isDirectory()) {
                continue;
            }

            migrateUserTemplates(file);
        }

        log.info("Templates migration finished !");
        log.info("--------------------------------------------------------------");
    }

    @SneakyThrows
    private void migrateUserTemplates(File file) {
        long userId = Long.parseLong(file.getName());
        log.debug("Migrating templates of {}", userId);

        User user = userService.rawOf(userId);

        for (File template : file.listFiles()) {
            JsonObject obj = new Gson().fromJson(new FileReader(template), JsonObject.class);

            Model.ModelBuilder builder = Model.builder();

            Guild g = guildService.of(obj.get("serverId").getAsLong());

            if (g.discordGuild() == null) {
                log.warn("The guild {} is not available, skipping template {}/{}", g.discordId(),
                         userId, template.getName());
                continue;
            }

            boolean isRoomHidden = obj.get("hidden").getAsBoolean();
            boolean isRoomPrivate = obj.get("private").getAsBoolean();

            builder.visibility(isRoomPrivate
                               ? RoomVisibility.PRIVATE
                               : isRoomHidden
                                 ? RoomVisibility.HIDDEN
                                 : RoomVisibility.PUBLIC);

            builder.channelName(obj.get("name").getAsString());
            builder.userLimit(obj.get("userLimit").getAsInt());

            addAllToBuilder(obj, "whitelistedUsers", userService::rawOf, u -> builder
                    .user(ModelUser.builder()
                                  .referringUser(u)
                                  .type(ModelEntityType.WHITELIST)
                                  .build()
                    )
            );

            addAllToBuilder(obj, "blacklistedUsers", userService::rawOf, u -> builder
                    .user(ModelUser.builder()
                                  .referringUser(u)
                                  .type(ModelEntityType.BLACKLIST)
                                  .build()
                    )
            );

            addAllToBuilder(obj, "moderators", userService::rawOf, u -> builder
                    .user(ModelUser.builder()
                                  .referringUser(u)
                                  .type(ModelEntityType.MODERATOR)
                                  .build()
                    )
            );

            addAllToBuilder(obj, "whitelistedRoles", i -> roleService.of(g, i), r -> builder
                    .role(ModelRole.builder()
                                  .referringRole(r)
                                  .type(ModelEntityType.WHITELIST)
                                  .build()
                    )
            );
            addAllToBuilder(obj, "blacklistedRoles", i -> roleService.of(g, i), r -> builder
                    .role(ModelRole.builder()
                                  .referringRole(r)
                                  .type(ModelEntityType.BLACKLIST)
                                  .build()
                    )
            );

            Template tmplt = templateService.save(
                    template.getName().replace(".json", ""),
                    user,
                    builder.build()
            );

            log.debug("Saved template {}", tmplt);
        }

        log.debug("Templates of {} migrated", userId);
    }

    private <T> void addAllToBuilder(JsonObject element, String name, Function<Long, T> transformer,
                                     Consumer<T> consumer) {
        JsonArray array = element.getAsJsonArray(name);
        for (JsonElement el : array) {
            T t = transformer.apply(el.getAsLong());
            consumer.accept(t);
        }
    }
}
