package org.reddev.pr.register;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
