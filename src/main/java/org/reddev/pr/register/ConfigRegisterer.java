/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.register;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings("unchecked") // Remove the JSON Errors that says to generify
public class ConfigRegisterer {
    public static void register(File configFile) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write("[bot]");
            writer.newLine();
            writer.write("langs = [\"en\", \"fr\"]");
            writer.newLine();
            writer.write("token = \"TOKEN HERE\"");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
