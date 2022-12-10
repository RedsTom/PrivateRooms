//TODO add copyright

package me.redstom.privaterooms.util.permission;

import net.dv8tion.jda.api.Permission;

import java.util.EnumSet;

public interface PermissionSet {

    EnumSet<Permission> NONE = EnumSet.noneOf(Permission.class);

    EnumSet<Permission> allowList();

    EnumSet<Permission> denyList();

    long allowRaw();

    long denyRaw();
}
