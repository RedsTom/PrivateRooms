package org.reddev.privateroomsreborn.utils

class ServerConfig {

    private String customPrefix, language, createChannelId, categoryId;
    private boolean disabled;

    ServerConfig(String customPrefix, String language, String createChannelId, String categoryId, boolean disabled) {
        this.customPrefix = customPrefix
        this.language = language
        this.createChannelId = createChannelId
        this.categoryId = categoryId
        this.disabled = disabled
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
