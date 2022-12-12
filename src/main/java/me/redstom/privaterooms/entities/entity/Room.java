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

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.redstom.privaterooms.entities.entity.model.Model;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

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

    private Room(Room r) {
        this(r.id, r.discordId, r.discordChannel, r.guild, Model.copyOf(r.model));
    }

    public static Room copyOf(Room room) {
        return new Room(room);
    }
}
