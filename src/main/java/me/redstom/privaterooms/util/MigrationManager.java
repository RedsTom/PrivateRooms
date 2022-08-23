package me.redstom.privaterooms.util;

import com.moandjiezana.toml.Toml;
import lombok.RequiredArgsConstructor;
import me.redstom.privaterooms.db.entity.Guild;
import me.redstom.privaterooms.db.repository.GuildRepository;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class MigrationManager {

    private final GuildRepository guildRepository;

    public void run() {
        System.out.println("""
          --------------------------------------------------------------
          Detected old private rooms config and saves !
          Those are not supported anymore ! Starting migration...
                    
          Please note that the old configuration will not be migrated !
          The migrated elements will be :
           - The templates
           - The server configurations
          --------------------------------------------------------------
          """);

        this.migrateServers();
    }

    private void migrateServers() {
        System.out.println("----------------------------------------------------");
        System.out.println("Migrating servers...");

        File serverFolder = new File("servers");

        if (!serverFolder.exists()) {
            System.out.println("No servers found, skipping migration...");
            return;
        }

        for (File file : serverFolder.listFiles()) {
            Toml toml = new Toml().read(file);

            long serverId = Long.parseLong(file.getName().replace(".toml", ""));
            String categoryId = toml.getString("categoryId");
            String createChannelId = toml.getString("createChannelId", "0");

            if (categoryId.isEmpty() || createChannelId.isEmpty()) {
                System.out.println("Skipping server " + serverId + " because it is not fully configured...");
                continue;
            }

            Guild guild = Guild.builder()
              .discordId(serverId)
              .categoryId(Long.parseLong(categoryId))
              .createChannelId(Long.parseLong(createChannelId))
              .build();

            guildRepository.save(guild);
            System.out.println("Migrated server " + serverId);
        }

        System.out.println("Servers migration finished !");
        System.out.println("----------------------------------------------------");
    }
}
