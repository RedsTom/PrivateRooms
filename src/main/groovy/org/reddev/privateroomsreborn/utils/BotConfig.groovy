package org.reddev.privateroomsreborn.utils

import org.hjson.JsonArray

class BotConfig {

    String token = ""
    String defaultPrefix = ""
    JsonArray languages
    Map<String, String> botOps = new HashMap<>()

    @Override
    String toString() {
        return "BotConfig{ token=$token ; languages=$languages ; botOps=$botOps }"
    }

}
