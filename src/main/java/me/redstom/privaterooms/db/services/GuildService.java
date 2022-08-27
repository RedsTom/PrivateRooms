package me.redstom.privaterooms.db.services;

import lombok.RequiredArgsConstructor;
import me.redstom.privaterooms.db.entity.Guild;
import me.redstom.privaterooms.db.repository.GuildRepository;
import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Service;

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
}
