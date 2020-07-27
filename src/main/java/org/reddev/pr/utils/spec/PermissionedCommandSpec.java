/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.utils.spec;

import fr.il_totore.ucp.CommandSpec;
import org.javacord.api.entity.permission.Permissions;

import java.util.Optional;

public class PermissionedCommandSpec<T> extends CommandSpec.ImplicitSpec<T> {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Permissions> permissions = Optional.empty();

    public PermissionedCommandSpec(String name) {
        super(name);
    }

    public ImplicitSpec<T> permissionized(Permissions permissions) {
        this.permissions = Optional.of(permissions);
        return this;
    }

    public Optional<Permissions> getPermissions() {
        return permissions;
    }
}
