package org.reddev.privateroomsreborn.utils

class BotConfig {

    String token = ""
    String defaultPrefix = ""
    List<String> languages
    Map<String, Object> botOps = new HashMap<>()

    @Override
    String toString() {
        return "BotConfig{ token=$token ; languages=$languages ; botOps=$botOps }"
    }

}
