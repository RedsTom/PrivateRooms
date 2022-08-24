package me.redstom.privaterooms.db.services;

import lombok.RequiredArgsConstructor;
import me.redstom.privaterooms.db.entity.User;
import me.redstom.privaterooms.db.repository.UserRepository;
import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JDA client;

    public User init(long userId) {
        return userRepository.save(User.builder()
          .discordId(userId)
          .templates(new ArrayList<>())
          .build());
    }

    public User rawOf(long userId) {
        return userRepository
          .findByDiscordId(userId)
          .orElseGet(() -> init(userId));
    }

    public User of(long discordId) {
        return of(rawOf(discordId));
    }

    public User of(User u) {
        return u.discordUser(client.retrieveUserById(u.discordId()).complete());
    }
}
