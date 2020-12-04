package org.reddev.privateroomsreborn.utils.general

import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import org.javacord.api.entity.server.Server
import org.reddev.privateroomsreborn.utils.ServerConfig

class ConfigUtils {

    static ServerConfig getServerConfig(Server guild) {
        File configFile = getServerConfigFile(guild)

        Toml toml = new Toml().read(configFile)
        return toml.to(ServerConfig.class)
    }

    private static File getServerConfigFile(Server guild) {

        File configFolder = new File(System.getProperty("user.dir"), "servers/")
        if (!configFolder.exists())
            configFolder.mkdir()

        File configFile = new File(
                configFolder,
                "${guild.idAsString}.toml"
        )

        if (!configFile.exists())
            initServerConfig(configFile)

        return configFile
    }

    private static def initServerConfig(File configFile) {

        configFile.createNewFile()

        BufferedWriter writer = new BufferedWriter(
                new FileWriter(configFile)
        )
        String defaultConfig = """|customPrefix = ""
                                  |language = "us"
                                  |createChannelId = ""
                                  |categoryId = ""
                                  |disabled = false
                                  """.stripMargin()

        defaultConfig.readLines().forEach {line ->
            writer.write(line)
            writer.newLine()
        }

        writer.flush()
        writer.close()

    }

    static void update(Server guild, ServerConfig config) {
        TomlWriter writer = new TomlWriter.Builder()
                .indentValuesBy(2)
                .indentTablesBy(4)
                .padArrayDelimitersBy(3)
                .build()

        writer.write(config, getServerConfigFile(guild))
    }
}
