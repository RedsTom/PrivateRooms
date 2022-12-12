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

import java.util.EnumSet;
import me.redstom.privaterooms.util.permission.PermissionSet;
import net.dv8tion.jda.api.Permission;

public enum ModelEntityType implements PermissionSet {
    WHITELIST(
            EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK,
                    Permission.VOICE_START_ACTIVITIES, Permission.VOICE_STREAM,
                    Permission.VOICE_USE_VAD),
            NONE),
    BLACKLIST(
            NONE,
            Permission.getPermissions(
                    Permission.ALL_VOICE_PERMISSIONS | Permission.VIEW_CHANNEL.getRawValue())),
    MODERATOR(
            Permission.getPermissions(Permission.ALL_VOICE_PERMISSIONS),
            NONE);

    private final EnumSet<Permission> allowList;
    private final EnumSet<Permission> denyList;
    private final long                allowRaw;
    private final long                denyRaw;

    ModelEntityType(EnumSet<Permission> allow, EnumSet<Permission> deny) {
        this.allowList = allow;
        this.denyList = deny;
        this.allowRaw = Permission.getRaw(allow);
        this.denyRaw = Permission.getRaw(deny);
    }

    @Override
    public EnumSet<Permission> allowList() {
        return allowList;
    }

    @Override
    public EnumSet<Permission> denyList() {
        return denyList;
    }

    @Override
    public long allowRaw() {
        return allowRaw;
    }

    @Override
    public long denyRaw() {
        return denyRaw;
    }
}
