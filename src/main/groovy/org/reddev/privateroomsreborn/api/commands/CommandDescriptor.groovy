package org.reddev.privateroomsreborn.api.commands

import groovy.transform.TupleConstructor
import org.javacord.api.entity.permission.PermissionType

@TupleConstructor
class CommandDescriptor {

    String description = "NOT SET"
    String usage = ""
    PermissionType[] permissions = []
    boolean hidden = false

}