/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.register;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.reddev.pr.Main;

import java.io.FileReader;
import java.io.IOException;

public class LangReader {

    public static void register() {

        try {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(new FileReader(Main.getLangFile().getAbsolutePath()));

            Main.langs = (JSONArray) obj.get("langs");
            Main.token = (String) obj.get("token");
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

}
