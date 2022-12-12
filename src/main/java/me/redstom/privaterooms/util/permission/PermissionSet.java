//TODO add copyright

package me.redstom.privaterooms.util.permission;

import java.util.EnumSet;
import net.dv8tion.jda.api.Permission;

public interface PermissionSet {

    EnumSet<Permission> NONE = EnumSet.noneOf(Permission.class);

    EnumSet<Permission> allowList();

    EnumSet<Permission> denyList();

    long allowRaw();

    long denyRaw();
}
