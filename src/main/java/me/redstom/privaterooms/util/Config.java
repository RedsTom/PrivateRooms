package me.redstom.privaterooms.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

@Data
@AllArgsConstructor
public class Config {
    private String token;
    private DatabaseConfig database;

    @Data
    @AllArgsConstructor
    public static class DatabaseConfig {
        private String host;
        private int port;
        private String username;
        private String password;
        private String database;
    }
}
