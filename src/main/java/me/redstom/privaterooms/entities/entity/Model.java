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

package me.redstom.privaterooms.entities.entity;

import lombok.*;
import me.redstom.privaterooms.util.room.RoomVisibility;

import javax.persistence.*;
import java.util.List;

@Embeddable

@Getter
@Setter

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Model {

    @Column(nullable = false)
    private String channelName;

    @Builder.Default
    @Column(nullable = false)
    private int maxUsers = 99;

    @Builder.Default
    @Column(nullable = false)
    private RoomVisibility visibility = RoomVisibility.PUBLIC;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "whitelisted_users")
    @Singular
    private List<User> whitelistUsers;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "blacklisted_users")
    @Singular
    private List<User> blacklistUsers;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "moderator_users")
    @Singular
    private List<User> moderatorUsers;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "whitelisted_roles")
    @Singular
    private List<Role> whitelistRoles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "blacklisted_roles")
    @Singular
    private List<Role> blacklistRoles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "template_moderator_roles")
    @Singular
    private List<Role> moderatorRoles;

}
