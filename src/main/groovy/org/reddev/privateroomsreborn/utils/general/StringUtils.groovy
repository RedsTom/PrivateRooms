package org.reddev.privateroomsreborn.utils.general

class StringUtils {

    static String j(String txt, Object... formats) {
        return String.format(txt, formats)
    }
}
