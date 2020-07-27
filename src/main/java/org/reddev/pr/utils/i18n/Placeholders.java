/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.utils.i18n;

public enum Placeholders {

    E_ACCENT_AIGU("%e%", "é"),
    E_ACCENT_GRAVE("%ee%", "è"),
    E_ACCENT_CIRCONFLEXE("%eee%", "ê"),
    A_ACCENT_AIGU("%a%", "á"),
    A_ACCENT_GRAVE("%aa%", "à"),
    C_CEDILLE("%c%", "ç"),
    N_TIDLE("%n%", "ñ"),
    ARROW_RIGHT("->", "→"),
    ARROW_LEFT("<-", "←"),
    ARROW_UP("a!", "↑"),
    ARROW_DOWN("!a", "↓"),
    S_CIRCONFLEXE("%s%", "ŝ"),
    G_CIRCONFLEXE("%g%", "ĝ"),
    C_MAJ_CIRCONFLEXE("%C%", "Ĉ"),
    J_CIRCONFLEXE("%j%", "ĵ"),
    NLINE("%n", "\n"),
    DOLLAR_S("$s", "%s");

    private final String base, replacement;

    Placeholders(String base, String replacement) {
        this.base = base;
        this.replacement = replacement;
    }

    public String getBase() {
        return base;
    }

    public String getReplacement() {
        return replacement;
    }
}
