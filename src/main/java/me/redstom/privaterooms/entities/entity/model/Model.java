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

package me.redstom.privaterooms.entities.entity.model;

import lombok.*;
import me.redstom.privaterooms.util.room.RoomVisibility;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToMany;
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

    @ManyToMany(cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.FALSE)
    @Singular
    private List<ModelUser> users;

    @ManyToMany(cascade = { CascadeType.ALL })
    @LazyCollection(LazyCollectionOption.FALSE)
    @Singular
    private List<ModelRole> roles;
}
