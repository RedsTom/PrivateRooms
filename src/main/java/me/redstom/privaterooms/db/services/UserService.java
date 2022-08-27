/*
 * PrivateRooms is a discord bot to manage vocal chats.
 * Copyright (C) 2022 GravenDev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
