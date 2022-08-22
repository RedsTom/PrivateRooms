package me.redstom.privaterooms;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import lombok.Getter;
import lombok.SneakyThrows;
import me.redstom.privaterooms.util.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.EnumSet;

@Component
public class PrivateRooms {

    @Getter
    private boolean initialized = false;

    @Autowired
    private ApplicationContext ctx;

    private JDA client;

    @Getter
    private Config config;

    public void init() {
        initConfig();
        initDb();

        initialized = true;
    }

    @SneakyThrows
    private void initConfig() {
        File configFile = new File("config.toml");

        if (!configFile.exists()) {
            configFile.createNewFile();

            Config config = new Config(
              "TOKEN HERE",
              new Config.DatabaseConfig(
                "localhost",
                5432,
                "postgres",
                "postgres",
                "postgres"
              )
            );

            TomlWriter writer = new TomlWriter();
            writer.write(config, configFile);

            System.err.println("Config file created at " + configFile.getAbsolutePath());
            System.err.println("Please fill in the config file and restart the bot");

            System.exit(1);
        }

        Toml cfg = new Toml();
        cfg.read(configFile);

        this.config = cfg.to(Config.class);
    }

    private void initDb() {
    }


    @SneakyThrows
    public void run() {
        this.client = JDABuilder
          .createDefault(config.token())
          .enableIntents(EnumSet.allOf(GatewayIntent.class))
          .build();


    }

}
