package me.redstom.privaterooms.db.repository;

import me.redstom.privaterooms.db.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByDiscordId(long discordId);

}
