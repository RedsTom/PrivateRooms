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
        User u = User.builder()
          .discordId(userId)
          .templates(new ArrayList<>())
          .build();

        return save(u);
    }

    public User rawOf(long userId) {
        return userRepository
          .findByDiscordId(userId)
          .orElseGet(() -> init(userId));
    }

    public User save(User u) {
        net.dv8tion.jda.api.entities.User user = client.retrieveUserById(u.discordId())
          .complete();
        u.discordUser(user);

        return userRepository.save(u);
    }

    public User of(long discordId) {
        User u = rawOf(discordId);

        net.dv8tion.jda.api.entities.User user = client.retrieveUserById(u.discordId())
          .complete();
        u.discordUser(user);

        return u;
    }
}
