package org.reddev.privateroomsreborn.utils

enum ETerminalColors {

    DEFAULT("\u001b[39m"),
    RESET("\u001b[21m"),
    BOLD("\u001b[1m"),
    DIM("\u001b[2m"),
    UNDERLINE("\u001b[4m"),
    BLINK("\u001b[5m"),
    REVERSE("e[7m"),
    HIDDEN("\u001b[8m"),
    BLACK("\u001b[30m"),
    RED("\u001b[31m"),
    GREEN("\u001b[32m"),
    YELLOW("\u001b[33m"),
    BLUE("\u001b[34m"),
    MAGENTA("\u001b[35m"),
    CYAN("\u001b[36m"),
    GRAY("\u001b[90m"),
    LIGHT_GRAY("\u001b[37m"),
    LIGHT_RED("\u001b[91m"),
    LIGHT_GREEN("\u001b[92m"),
    LIGHT_YELLOW("\u001b[93m"),
    LIGHT_BLUE("\u001b[94m"),
    LIGHT_MAGENTA("\u001b[95m"),
    LIGHT_CYAN("\u001b[96m"),
    WHITE("\u001b[97m");

    private String seq;

    ETerminalColors(String seq) {
        this.seq = seq
    }

    String getSeq() {
        return seq
    }

    @Override
    String toString() {
        return seq
    }

    static String c(String txt) {
        return txt + DEFAULT
    }
}
