package org.reddev.privateroomsreborn

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import groovy.transform.CompileStatic
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.activity.ActivityType
import org.javacord.api.entity.user.UserStatus
import org.reddev.privateroomsreborn.commands.utils.CommandManager
import org.reddev.privateroomsreborn.events.VoiceJoinListener
import org.reddev.privateroomsreborn.events.VoiceLeaveListener
import org.reddev.privateroomsreborn.utils.BotConfig
import org.reddev.privateroomsreborn.utils.general.LangUtils
import org.simpleyaml.configuration.file.YamlConfiguration

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
        File configFile = new File(System.getProperty('user.dir'), 'config.yml')

        if (!configFile.exists()) {
            println(c("""
                    | -------------------------------------------------------------------------------------
                    | $DEFAULT[${YELLOW}WARNING$DEFAULT] ${CYAN}File ${BLUE}config.yml$CYAN not found ! Creating it...
                    | $DEFAULT[${YELLOW}WARNING$DEFAULT] ${RED}The bot will not start because of the certain error that will happen$DEFAULT
                    | -------------------------------------------------------------------------------------
                    """.stripMargin()))
            configFile.createNewFile()
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(configFile)
            )
            String defaultConfig = '''
                    |# Token of the bot
                    |token: TOKEN HERE
                    |prefix: %
                    |languages:
                    |   - us
                    |   - fr
                    |# Admins of the bot
                    |bot-ops:
                    |   # Enter the ID of the op as key, and the discriminator as value
            '''.stripMargin()

            defaultConfig.readLines().forEach { line ->
                writer.write(line)
                writer.newLine()
            }

            writer.flush()
            writer.close()
            return false
        } else {
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(configFile)
            config.token = configuration.getString("token")
            config.defaultPrefix = configuration.getString("prefix")
            config.languages = configuration.getStringList("languages")
            config.botOps = configuration.getConfigurationSection("bot-ops").getMapValues(false)
            return true
        }
    }

}
