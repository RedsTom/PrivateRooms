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

public interface ModelEntity {

    ModelEntityType type();

    @Entity
    @Table

    @Getter
    @Setter

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    class ModelRole implements ModelEntity {
        @Id @GeneratedValue
        private Long id;

        @ManyToOne
        private me.redstom.privaterooms.entities.entity.Role referringRole;

        @Column
        private ModelEntityType type;

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("ModelRole{");
            sb.append("id=").append(id);
            sb.append(", referringRole=").append(referringRole);
            sb.append(", type=").append(type);
            sb.append('}');
            return sb.toString();
        }
    }

    @Entity
    @Table

    @Getter
    @Setter

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    class ModelUser implements ModelEntity {

        @Id @GeneratedValue
        private Long id;

        @ManyToOne
        private User referringUser;

        @Column
        private ModelEntityType type;

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("ModelUser{");
            sb.append("id=").append(id);
            sb.append(", referringUser=").append(referringUser);
            sb.append(", type=").append(type);
            sb.append('}');
            return sb.toString();
        }
    }
}
