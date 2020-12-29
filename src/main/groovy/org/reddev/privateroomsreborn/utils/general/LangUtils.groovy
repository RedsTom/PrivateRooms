package org.reddev.privateroomsreborn.utils.general

import org.javacord.api.entity.server.Server
import org.reddev.privateroomsreborn.utils.BotConfig
import org.simpleyaml.configuration.file.YamlConfiguration

class LangUtils {

    static Map<String, YamlConfiguration> languageCache = new HashMap<>()

    static void updateLanguageCache(BotConfig config) {
        for (String lang in config.languages) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(getLanguageFile(lang))
            println(yamlConfiguration.getString("test"))
            println(yamlConfiguration.getString("tests.success"))
            languageCache.put(lang, yamlConfiguration)
        }
    }

    static String l(String key, Server guild) {
        return l(key, getLang(guild))
    }

    static String l(String key, String lang) {
        return languageCache.get(lang).getString(key, key)
    }

    static void createLangFiles(BotConfig config) {
        File languagesFolder = new File(System.getProperty("user.dir"), "languages/")
        if (!(languagesFolder.exists())) languagesFolder.mkdir()
        config.languages.forEach { language ->
            File languageFile = new File(languagesFolder, "${language}.yml")
            if (!languageFile.exists()) languageFile.createNewFile()
        }
    }

    static String getLang(Server guild) {
        return ConfigUtils.getServerConfig(guild).language
    }

    static File getLanguageFile(String language) {
        File languagesFolder = new File(System.getProperty("user.dir"), "languages/")
        if (!(languagesFolder.exists())) languagesFolder.mkdir()
        File languageFile = new File(languagesFolder, "${language}.yml")
        if (!languageFile.exists()) languageFile.createNewFile()
        return languageFile
    }

}
