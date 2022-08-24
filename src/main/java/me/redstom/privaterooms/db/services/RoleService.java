package me.redstom.privaterooms.db.services;

import lombok.RequiredArgsConstructor;
import me.redstom.privaterooms.db.entity.Guild;
import me.redstom.privaterooms.db.entity.Role;
import me.redstom.privaterooms.db.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final GuildService guildService;

    public Role init(Guild g, long roleId) {
        return roleRepository.save(Role.builder()
          .discordId(roleId)
          .guild(g)
          .build());
    }

    public Role rawOf(Guild g, long roleId) {
        return roleRepository
          .findByDiscordId(roleId)
          .orElseGet(() -> init(g, roleId));
    }

    public Role of(Guild g, long discordId) {
        return of(rawOf(g, discordId));
    }

    public Role of(Role r) {
        Guild g = guildService.of(r.guild());
        return r.discordRole(g.discordGuild().getRoleById(r.discordId()));
    }
}
