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
        Role r = Role.builder()
          .discordId(roleId)
          .guild(g)
          .build();

        return save(r);
    }

    public Role rawOf(Guild g, long roleId) {
        return roleRepository
          .findByDiscordId(roleId)
          .orElseGet(() -> init(g, roleId));
    }

    public Role save(Role r) {
        return roleRepository.save(r);
    }

    public Role of(Guild g, long discordId) {
        Role r = rawOf(g, discordId);
        return of(r);
    }

    public Role of(Role r) {
        Guild g = guildService.of(r.guild());

        net.dv8tion.jda.api.entities.Role role = g.discordGuild().getRoleById(r.discordId());
        r.discordRole(role);

        return r;
    }
}
