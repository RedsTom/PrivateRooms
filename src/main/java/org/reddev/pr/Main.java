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
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.simple.JSONArray;
import org.reddev.pr.register.CommandRegister;
import org.reddev.pr.register.EventRegistry;
import org.reddev.pr.register.LangReader;
import org.reddev.pr.register.LangRegisterer;
import org.reddev.pr.utils.sql.DatabaseManager;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static int ownerPermission = 66061056;
    public static int userPermission = 3146752;
    public static int userDeniedPermission = 62914816;
    public static int deniedPermission = 30408960;
    public static JSONArray langs;
    private static DiscordApi api;
    private static final CommandRegistry<MessageCreateEvent> registry = new PrefixedCommandRegistry<>(Lists.newArrayList(), "%");
    private static DatabaseManager databaseManager;
    private static File langFile;
    public static String token;

    public static void main(String[] args) throws SQLException, IOException {

        databaseManager = new DatabaseManager();
        databaseManager.openConnection();
        langFile = new File(System.getProperty("user.dir"), "config.json");
        if (!(langFile.exists())) {
            langFile.createNewFile();
            new LangRegisterer(langFile);
        }

        try {
            new LangReader();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //API DEFINITION
        {
            api = new DiscordApiBuilder().setToken(token).login().join();
        }

        api.updateActivity("%help | Your Private Channels");

        Main.getDatabaseManager().createTableIfNotExists("servers", "id INT(255) UNIQUE, createChannelId INT(255) UNIQUE, categoryId INT(255) UNIQUE, lang VARCHAR(255)");

        new CommandRegister(registry);
        new EventRegistry(api, registry);
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
