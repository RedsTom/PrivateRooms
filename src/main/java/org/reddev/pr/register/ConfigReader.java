/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.register;

import com.moandjiezana.toml.Toml;
import org.reddev.pr.Main;

public class ConfigReader {

    public static void register() {
        Toml toml = new Toml().read(Main.getLangFile());

        Main.langs = toml.getList("bot.langs");
        Main.token = toml.getString("bot.token");
    }

}
