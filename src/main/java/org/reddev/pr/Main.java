/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr;

import com.google.common.collect.Lists;
import fr.il_totore.ucp.registration.CommandRegistry;
import fr.il_totore.ucp.registration.PrefixedCommandRegistry;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.event.message.MessageCreateEvent;
import org.reddev.pr.register.CommandRegister;
import org.reddev.pr.register.ConfigRegisterer;
import org.reddev.pr.register.EventRegistry;
import org.reddev.pr.register.LangReader;
import org.reddev.pr.utils.sql.DatabaseManager;

import java.io.File;
import java.util.List;

public class Main {

    public static List<Object> langs;
    private static final CommandRegistry<MessageCreateEvent> registry = new PrefixedCommandRegistry<>(Lists.newArrayList(), "%");
    private static DatabaseManager databaseManager;
    private static File langFile;
    public static String token;

    public static void main(String[] args) {

        databaseManager = new DatabaseManager();
        databaseManager.openConnection();
        langFile = new File(System.getProperty("user.dir"), "config.toml");
        if (!(langFile.exists())) {
            try {
                //noinspection ResultOfMethodCallIgnored -> Ignores the result of langFile.createNewFile();
                langFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ConfigRegisterer.register(langFile);
        }

        LangReader.register();
        //TEST 2
        //API DEFINITION
        DiscordApi api;
        {
            api = new DiscordApiBuilder().setToken(token).login().join();
        }

        System.out.println(api.createBotInvite(Permissions.fromBitmask(8)));

        api.updateActivity("%help | by @RedsTom#4985");

        Main.getDatabaseManager().createTableIfNotExists("servers", "id INT(255) UNIQUE, createChannelId INT(255) UNIQUE, categoryId INT(255) UNIQUE, lang VARCHAR(255)");

        CommandRegister.register(registry);
        EventRegistry.register(api, registry);
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static File getLangFile() {
        return langFile;
    }

    public static CommandRegistry<MessageCreateEvent> getRegistry() {
        return registry;
    }
}
