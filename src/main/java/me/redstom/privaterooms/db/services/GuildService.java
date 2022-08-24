package me.redstom.privaterooms.db.services;

import lombok.RequiredArgsConstructor;
import me.redstom.privaterooms.db.entity.Guild;
import me.redstom.privaterooms.db.repository.GuildRepository;
import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuildService {

    private final GuildRepository guildRepository;
    private final JDA client;

    public Guild init(long discordId) {
        Guild g = Guild.builder()
          .discordId(discordId)
          .build();

        return save(g);
    }

    public Guild rawOf(long discordId) {
        return guildRepository
          .findByDiscordId(discordId)
          .orElseGet(() -> init(discordId));
    }

    public Guild save(Guild g) {
        return guildRepository.save(g);
    }

    public Guild of(long discordId) {
        Guild g = rawOf(discordId);
        return of(g);
    }

    public Guild of(Guild g) {
        net.dv8tion.jda.api.entities.Guild guild = client.getGuildById(g.discordId());
        g.discordGuild(guild);

        return g;
    }

    public void registerChannelsFor(Guild guild, long categoryId, long createChannelId) {
        guild.categoryId(categoryId);
        guild.createChannelId(createChannelId);

        guildRepository.save(guild);
    }
}
