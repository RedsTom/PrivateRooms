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

package me.redstom.privaterooms.entities.services;

import lombok.RequiredArgsConstructor;
import me.redstom.privaterooms.entities.entity.Guild;
import me.redstom.privaterooms.entities.repository.GuildRepository;
import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@Service
@RequiredArgsConstructor
public class GuildService {

    private final GuildRepository guildRepository;
    private final JDA client;

    public Guild init(long discordId) {
        return guildRepository.save(Guild.builder()
          .discordId(discordId)
          .build());
    }

    public Guild rawOf(net.dv8tion.jda.api.entities.Guild guild) {
        return rawOf(guild.getIdLong());
    }

    public Guild rawOf(long discordId) {
        return guildRepository
          .findByDiscordId(discordId)
          .orElseGet(() -> init(discordId));
    }

    public Guild of(long discordId) {
        return of(rawOf(discordId));
    }

    public Guild of(Guild g) {
        return g.discordGuild(client.getGuildById(g.discordId()));
    }

    public Guild update(long guildId, UnaryOperator<Guild> update) {
        return guildRepository.save(update.apply(of(guildId)));
    }

    public Guild update(Guild g, UnaryOperator<Guild> update) {
        return guildRepository.save(update.apply(g));
    }

    public void all(Consumer<Guild> consumer) {
        guildRepository.findAll().forEach(consumer);
    }
}
