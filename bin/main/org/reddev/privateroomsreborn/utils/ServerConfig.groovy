package org.reddev.privateroomsreborn.utils

class ServerConfig {

    private String customPrefix, language, createChannelId, categoryId;
    private boolean disabled;
    private List<Long> whitelistedVoiceChannels = []

    ServerConfig(String customPrefix, String language, String createChannelId, String categoryId, boolean disabled, List<Long> whitelistedVoiceChannels) {
        this.customPrefix = customPrefix
        this.language = language
        this.createChannelId = createChannelId
        this.categoryId = categoryId
        this.disabled = disabled
        this.whitelistedVoiceChannels = whitelistedVoiceChannels;
    }

    String getCustomPrefix() {
        return customPrefix
    }

    boolean getDisabled() {
        return disabled
    }

    void setDisabled(boolean disabled) {
        this.disabled = disabled
    }

    List<Long> getWhitelistedVoiceChannels() {
        return whitelistedVoiceChannels
    }

    void setWhitelistedVoiceChannels(List<Long> whitelistedVoiceChannels) {
        this.whitelistedVoiceChannels = whitelistedVoiceChannels
    }

    String getCustomPrefix(BotConfig config) {
        return customPrefix ? customPrefix : config.defaultPrefix
    }

    void setCustomPrefix(String customPrefix) {
        this.customPrefix = customPrefix
    }

    String getLanguage() {
        return language ? language : "us"
    }

    void setLanguage(String language) {
        this.language = language
    }

    String getCreateChannelId() {
        return createChannelId ? createChannelId : "NOT SET"
    }

    void setCreateChannelId(String createChannelId) {
        this.createChannelId = createChannelId
    }

    String getCategoryId() {
        return categoryId ? categoryId : "NOT SET"
    }

    void setCategoryId(String categoryId) {
        this.categoryId = categoryId
    }

    boolean getDeleted() {
        return disabled
    }

    void setDeleted(boolean deleted) {
        this.disabled = deleted
    }
}
