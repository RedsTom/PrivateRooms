package org.reddev.pr.command.configsubs;

import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.reddev.pr.EmbedUtils;
import org.reddev.pr.command.configsubs.utils.ConfigSubCommandExecutor;
import org.reddev.pr.command.configsubs.utils.MatchRoleQuery;
import org.reddev.pr.utils.i18n.I18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class SubCommandRemoveRole implements org.reddev.pr.command.configsubs.utils.ConfigSubCommandExecutor {
    @Override
    public void execute(Server server, User user, ServerVoiceChannel voiceChannel, TextChannel textChannel, Map<String, ConfigSubCommandExecutor> subs, String[] args, MessageCreateEvent event) {

        if (args.length != 1) {
            event.getChannel().sendMessage(EmbedUtils.getErrorEmbed(I18n.format(event.getMessage().getServer().get().getId(), "command.config.remove-role.error.no_role_precised"), event.getMessage().getServer().get()));
            return;
        }

        List<Role> roleMatches = new ArrayList<>();
        int matchFound = 0;
        String roleQuery = args[0];
        for (Role role : server.getRoles()) {
            if (role.getName().contains(roleQuery)) {
                matchFound++;
                roleMatches.add(role);
            }
        }

        AtomicReference<Role> r = new AtomicReference<>();
        new MatchRoleQuery(textChannel, roleMatches).then((role) -> {
            r.set(role);
            voiceChannel.createUpdater()
                    .removePermissionOverwrite(
                            r.get()
                    )
                    .update();

            textChannel.sendMessage(
                    EmbedUtils.getSuccessEmbed(
                            I18n.format(
                                    server.getId(),
                                    "command.config.remove-role.successful.title"),
                            String.format(I18n.format(server.getId(),
                                    "command.config.remove-role.successful.description"),
                                    r.get().getName()
                            )
                    )
            );
        }).start();

    }

    @Override
    public String getDescription(Server server) {
        return I18n.format(server.getId(), "command.config.remove-role.description");
    }

    @Override
    public String getUsage() {
        return "<role name>";
    }
}
