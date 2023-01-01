# ENGLISH

error-title = An error occurred
error-description = **{$message}**

# COMMANDS
commands-ping-name = ping
commands-ping-description = Show the ping of the bot

commands-ping-embed-title = Pong !
commands-ping-embed-description =
    Gateway ping: `{$gateway}`ms
    API ping: `{$rest}`ms

commands-config-user = user
commands-config-role = role

commands-config-name = config
commands-config-description = Configure the current room

commands-config-group-name = {$name}
commands-config-group-description = Edit the {$name} configuration

commands-config-group-add-name = add
commands-config-group-add-description = Add an user to the {$name}

commands-config-group-add-role-name = add-role
commands-config-group-add-role-description = Add a role to the {$name}

commands-config-group-remove-name = remove
commands-config-group-remove-description = Remove an user from the {$name}

commands-config-group-remove-role-name = remove-role
commands-config-group-remove-role-description = Remove a role from the {$name}

commands-config-group-list-name = list
commands-config-group-list-description = Show the {$name ->
    [moderators] list of moderators
   *[other] {$name}
}