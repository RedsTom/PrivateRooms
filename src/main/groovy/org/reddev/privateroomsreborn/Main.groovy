package org.reddev.privateroomsreborn

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import groovy.transform.CompileStatic
import org.hjson.JsonObject
import org.hjson.JsonValue
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.activity.ActivityType
import org.javacord.api.entity.user.UserStatus
import org.reddev.privateroomsreborn.commands.utils.CommandManager
import org.reddev.privateroomsreborn.events.VoiceJoinListener
import org.reddev.privateroomsreborn.events.VoiceLeaveListener
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.general.LangUtils

import static org.reddev.privateroomsreborn.utils.ETerminalColors.*

@CompileStatic
class Main {

    static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create()

    static void main(String[] args) {

        BotConfig config = new BotConfig()
        if (initHjsonConfig(config)) {
            LangUtils.createLangFiles(config)
            LangUtils.updateLanguageCache(config)

            DiscordApi api = new DiscordApiBuilder().setToken(config.token).login().join()

            api.addMessageCreateListener { CommandManager.onMessage(it, config) }
            api.addServerVoiceChannelMemberJoinListener(new VoiceJoinListener())
            api.addServerVoiceChannelMemberLeaveListener(new VoiceLeaveListener())
            api.updateActivity(ActivityType.LISTENING, "${config.defaultPrefix}help | " + api.owner.get().discriminatedName)
            api.updateStatus(UserStatus.DO_NOT_DISTURB)
        }
    }

    static boolean initHjsonConfig(BotConfig config) {
        File configFile = new File(System.getProperty('user.dir'), 'config.hjson')

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
            String defaultConfig = '''
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
'''.stripMargin()

            defaultConfig.readLines().forEach { line ->
                writer.write(line)
                writer.newLine()
            }

            writer.flush()
            writer.close()
            return false
        } else {
            BufferedReader reader = new BufferedReader(new FileReader(configFile))
            String lines = reader.readLines().join('\n')

            JsonObject obj = JsonValue.readHjson(lines).asObject()
            config.token = obj.getString('token', '')
            config.defaultPrefix = obj.getString('prefix', '%')
            config.languages = new ArrayList<>()
            for (def language in obj.get('languages').asArray()) {
                config.languages.add(language.asString())
            }
            obj.get('bot-ops').asObject().forEach { member ->
                config.botOps[member.name] = member.value.asString()
            }
            return true
        }
    }

}
