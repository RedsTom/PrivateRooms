package me.redstom.privaterooms.db.repository;

import me.redstom.privaterooms.db.entity.Guild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuildRepository extends JpaRepository<Guild, Long> {

    Optional<Guild> findByDiscordId(long discordId);

}
