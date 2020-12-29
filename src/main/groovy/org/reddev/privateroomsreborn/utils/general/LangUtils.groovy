package org.reddev.privateroomsreborn.utils.general

import org.hjson.JsonObject
import org.hjson.JsonValue
import org.javacord.api.entity.server.Server
import org.reddev.privateroomsreborn.utils.BotConfig

import java.nio.charset.StandardCharsets

class LangUtils {

    static Map<String, JsonObject> languageCache = new HashMap<>()

    static void updateLanguageCache(BotConfig config) {
        for (String lang in config.languages) {
            println "-----[ ${lang} ]------"
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    getLanguageFile(
                                            lang
                                    )
                            ),
                            StandardCharsets.UTF_8
                    )
            )
            String lines = reader.readLines().join("\n")

            JsonObject languageObject = JsonValue.readHjson(lines).asObject()
            languageCache.put(lang, languageObject)
        }
    }

    static String l(String key, Server guild) {
        return l(key, getLang(guild))
    }

    static String l(String key, String lang) {
        return languageCache.getOrDefault(lang, new JsonObject()).getString(key, key)
    }

    static void createLangFiles(BotConfig config) {
        File languagesFolder = new File(System.getProperty("user.dir"), "languages/")
        if (!(languagesFolder.exists())) languagesFolder.mkdir()
        config.languages.forEach { language ->
            File languageFile = new File(languagesFolder, "${language}.hjson")
            if (!languageFile.exists()) languageFile.createNewFile()
        }
    }

    static String getLang(Server guild) {
        return ConfigUtils.getServerConfig(guild).language
    }

    static File getLanguageFile(String language) {
        File languagesFolder = new File(System.getProperty("user.dir"), "languages/")
        if (!(languagesFolder.exists())) languagesFolder.mkdir()
        File languageFile = new File(languagesFolder, "${language}.hjson")
        if (!languageFile.exists()) languageFile.createNewFile()
        return languageFile
    }

}
