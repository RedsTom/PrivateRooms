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

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import me.redstom.privaterooms.util.room.RoomVisibility;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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
    private int userLimit = 99;

    @Builder.Default
    @Column(nullable = false)
    private RoomVisibility visibility = RoomVisibility.PUBLIC;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.FALSE)
    @Singular
    private List<ModelUser> users;

    @ManyToMany(cascade = {CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.FALSE)
    @Singular
    private List<ModelRole> roles;

    private Model(Model model) {
        this.channelName = model.channelName;
        this.userLimit = model.userLimit;
        this.visibility = model.visibility;
        this.users = new ArrayList<>(model.users);
        this.roles = new ArrayList<>(model.roles);
    }

    public static Model copyOf(Model model) {
        return new Model(model);
    }

    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ModelUser modelUser = (ModelUser) o;
        return id != null && Objects.equals(id, modelUser.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
     */
}
