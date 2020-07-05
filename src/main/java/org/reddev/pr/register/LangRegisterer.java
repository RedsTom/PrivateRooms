/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.register;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings("unchecked") // Remove the JSON Errors that says to generify
public class LangRegisterer {
    public LangRegisterer(File langFile) {

        JSONObject obj = new JSONObject();
        obj.put("token", "TOKEN HERE");
        JSONArray langs = new JSONArray();
        langs.add("en");
        langs.add("fr");
        obj.put("langs", langs);
        FileWriter fw = null;

        try {
            fw = new FileWriter(langFile);
            fw.write(obj.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw != null) {
                    fw.flush();
                }
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
