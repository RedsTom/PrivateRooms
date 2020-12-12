package org.reddev.privateroomsreborn.utils.general

import org.hjson.JsonArray
import org.hjson.JsonValue

class ListUtils {

    static <T> boolean contains(T[] list, T value) {
        for (T t in list) {
            if (t == value) return true
        }
        return false
    }

    static <T> boolean contains(JsonArray list, T value) {
        for (JsonValue t in list.values()) {
            if (t == value) return true
        }
        return false
    }

}
