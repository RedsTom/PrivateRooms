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
import me.redstom.privaterooms.entities.entity.Role;
import me.redstom.privaterooms.entities.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final GuildService   guildService;

    public Role init(Guild g, long roleId) {
        return roleRepository.save(Role.builder().discordId(roleId).guild(g).build());
    }

    public Role rawOf(Guild g, long roleId) {
        return roleRepository.findByDiscordId(roleId).orElseGet(() -> init(g, roleId));
    }

    public Role of(Guild g, long discordId) {
        return of(rawOf(g, discordId));
    }

    public Role of(Role r) {
        Guild g = guildService.of(r.guild());
        return r.discordRole(g.discordGuild().getRoleById(r.discordId()));
    }
}
