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
import net.dv8tion.jda.api.entities.VoiceChannel;

import javax.persistence.*;

@Table(name = "rooms")
@Entity

@Getter
@Setter

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private long discordId;

    @Transient
    private VoiceChannel discordChannel;

    @ManyToOne
    private Guild guild;

    @Embedded
    private Model model;

    public Room(Room r) {
        this(r.id, r.discordId, r.discordChannel, r.guild, new Model(r.model));
    }
}
