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

import javax.persistence.*;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "guilds")

@Getter
@Setter

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Guild {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private long discordId;

    @Transient
    private net.dv8tion.jda.api.entities.Guild discordGuild;

    @Builder.Default
    @Column(nullable = false)
    private Locale locale = Locale.ENGLISH;

    private long categoryId;
    private long createChannelId;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<User> users;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Role> roles;
}
