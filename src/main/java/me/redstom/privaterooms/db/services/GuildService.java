package me.redstom.privaterooms.db.services;

import lombok.RequiredArgsConstructor;
import me.redstom.privaterooms.db.entity.Guild;
import me.redstom.privaterooms.db.repository.GuildRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuildService {

    private final GuildRepository guildRepository;

    public Guild init(long discordId) {
        Guild g = Guild.builder()
          .discordId(discordId)
          .build();

        return guildRepository.save(g);
    }

    public Guild of(long discordId) {
        return guildRepository
          .findByDiscordId(discordId)
          .orElseGet(() -> init(discordId));
    }

    public void registerChannelsFor(Guild guild, long categoryId, long createChannelId) {
        guild.categoryId(categoryId);
        guild.createChannelId(createChannelId);

        guildRepository.save(guild);
    }
}
