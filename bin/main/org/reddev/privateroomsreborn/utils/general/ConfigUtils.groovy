package org.reddev.privateroomsreborn.utils.general

import com.google.gson.Gson
import com.google.gson.stream.JsonWriter
import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import org.javacord.api.entity.server.Server
import org.reddev.privateroomsreborn.utils.ServerConfig
import org.reddev.privateroomsreborn.utils.channels.PrivateChannel

import java.util.concurrent.CompletableFuture

class ConfigUtils {

    static boolean saveTemplate(Gson gson, Server server, String name, String content) {
        def serverFolder = createTemplateFolder(server)

        File file = new File(serverFolder, "${name}.json")
        if (file.exists()) return false
        file.createNewFile()
        JsonWriter writer = gson.newJsonWriter(new FileWriter(file))
        writer.jsonValue(content)
        writer.flush()
        writer.close()
        return true
    }

    static CompletableFuture<Boolean> loadTemplate(Gson gson, Server server, String name, PrivateChannel channel) {
        CompletableFuture.supplyAsync {
            def serverFolder = createTemplateFolder(server)

            File file = new File(serverFolder, "${name}.json")
            if (!file.exists()) {
                return false
            }
            PrivateChannel newChannel = gson.fromJson(new FileReader(file), PrivateChannel.class)
            newChannel.serverId = channel.serverId
            newChannel.channelId = channel.channelId
            channel.reset()
            newChannel.update(server.api)
            return true
        }
    }

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

        defaultConfig.readLines().forEach { line ->
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

    static File createTemplateFolder(Server server) {
        File templatesFolder = new File(System.getProperty("user.dir"), "templates/")
        if (!templatesFolder.exists()) templatesFolder.mkdir()
        File serverFolder = new File(templatesFolder, "${server.id}/")
        if (!serverFolder.exists()) serverFolder.mkdir()
        serverFolder
    }
}
