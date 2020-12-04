package org.reddev.privateroomsreborn

import org.hjson.JsonObject
import org.hjson.JsonValue
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.reddev.privateroomsreborn.commands.utils.CommandManager
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.general.LangUtils

import static org.reddev.privateroomsreborn.utils.ETerminalColors.*

class Main {

    static void main(String[] args) {

        BotConfig config = new BotConfig()
        if (initHjsonConfig(config)) {
            LangUtils.createLangFiles(config)

            DiscordApi api = new DiscordApiBuilder().setToken(config.token).login().join()

            api.addMessageCreateListener { CommandManager.onMessage(it, config) }
            api.updateActivity("${config.defaultPrefix}help", "https://twitch.tv/mr_redstom")

        }
    }

    static def initHjsonConfig(BotConfig config) {

        File configFile = new File(System.getProperty("user.dir"), "config.hjson")

        if (!configFile.exists()) {
            println(c("""
                    | -------------------------------------------------------------------------------------
                    | $DEFAULT[${YELLOW}WARNING$DEFAULT] ${CYAN}File ${BLUE}config.hjson$CYAN not found ! Creating it...
                    | $DEFAULT[${YELLOW}WARNING$DEFAULT] ${RED}The bot will not start because of the certain error that will happen$DEFAULT
                    | -------------------------------------------------------------------------------------
                    """.stripMargin()))
            configFile.createNewFile()
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(configFile)
            )
            String defaultConfig = """
|{
|       "token": "TOKEN HERE", # Enter here the token of your bot
|       "prefix": "%",                                                          # Prefix of the bot
|       "languages": [                                                          # -------------- Languages --------------
|         "us", "fr"                                                            # Enter here your languages
|       ],                                                                      #
|       "bot-ops": {                                                            # -------------- Admins of the bot --------------
|         "723471302123323434": "9999"                                          # Enter the ID of the op as key and the tag as value
|       }
|}
""".stripMargin()

            defaultConfig.readLines().forEach { line ->
                writer.write(line)
                writer.newLine()
            }

            writer.flush()
            writer.close()
            return false
        } else {

            BufferedReader reader = new BufferedReader(new FileReader(configFile))
            String lines = reader.readLines().join("\n")

            JsonObject obj = JsonValue.readHjson(lines).asObject()
            config.token = obj.getString("token", "")
            config.defaultPrefix = obj.getString("prefix", "%")
            config.languages = obj.get("languages").asArray()
            obj.get("bot-ops").asObject().forEach { member ->
                config.botOps[member.name] = member.value.asString()
            }
            return true

        }
    }
}
