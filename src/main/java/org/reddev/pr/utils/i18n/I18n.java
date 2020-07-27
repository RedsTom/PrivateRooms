/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.utils.i18n;

import org.reddev.pr.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class I18n {

    public static String format(String locale, String key) {
        try {
            Properties properties = new Properties();
            String propFileName = locale + ".lang";
            File f = new File(System.getProperty("user.dir"), propFileName);
            if (!(f.exists())) f.createNewFile();
            InputStream stream = new FileInputStream(f);

            if (stream == null && !(locale.equals("en"))) {
                return I18n.format("en", key);
            } else if (stream == null) {
                throw new FileNotFoundException("The file " + propFileName + " cannot be found !");
            } else {
                properties.load(stream);
            }
            String result;
            result = properties.getProperty(key, key);
            for (Placeholders value : Placeholders.values()) {
                result = result.replace(value.getBase(), value.getReplacement());
            }
            return result.replace("\\n", "\n");
        } catch (Exception e) {
            return key;
        }
    }

    public static String format(long serverId, String key) {
        try {
            return format((String) Main.getDatabaseManager().getData("servers", "lang", "id=" + serverId), key);
        } catch (Exception e) {
            return key;
        }
    }
}
