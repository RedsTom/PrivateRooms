/*
 * Copyright (c)
 * RedDev 2020
 * Private Rooms Bot
 */

package org.reddev.pr.event;

import fr.il_totore.ucp.CommandContext;
import fr.il_totore.ucp.CommandSpec;
import fr.il_totore.ucp.parsing.ParsingResult;
import fr.il_totore.ucp.registration.CommandRegistry;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.reddev.pr.Main;
import org.reddev.pr.utils.i18n.I18n;
import org.reddev.pr.utils.spec.PermissionedCommandSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageCreateEventListener implements MessageCreateListener {

    private final CommandRegistry<MessageCreateEvent> registry;

    public MessageCreateEventListener(CommandRegistry<MessageCreateEvent> registry) {
        this.registry = registry;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {

        if (!event.getMessage().isServerMessage() && !event.getMessageAuthor().isBotUser()) {
            event.getChannel().sendMessage("Sorry, but I don't support DM commands !");
            return;
        }

        ParsingResult<MessageCreateEvent> parsingResult = registry.parse(event, event.getMessage().getContent());
        Optional<CommandContext<MessageCreateEvent>> context = parsingResult.getContext();
        if (!context.isPresent() || !parsingResult.getContext().isPresent()) return;
        CommandSpec<MessageCreateEvent> spec = parsingResult.getContext().get().getSpec();
        if (spec instanceof PermissionedCommandSpec) {
            PermissionedCommandSpec<MessageCreateEvent> permissionSpec = (PermissionedCommandSpec<MessageCreateEvent>) spec;
            if (permissionSpec.getPermissions().isPresent() && event.getServer().isPresent() && event.getMessageAuthor().isUser()) {
                List<PermissionType> permissions = new ArrayList<>(permissionSpec.getPermissions().get().getAllowedPermission());
                if (!event.getServer().get().hasPermissions(event.getMessageAuthor().asUser().get(), permissions.toArray(new PermissionType[0]))) {
                    event.getChannel().sendMessage(Main.getErrorEmbed(I18n.format(event.getServer().get().getId(), "error.no_perm"), event.getServer().get()));
                    return;
                }
            }
        }
        if (parsingResult.getResultType() == ParsingResult.ResultType.FAILURE) {
            event.getChannel().sendMessage(Main.getErrorEmbed(parsingResult.getMessage().get(), event.getServer().get()));
            return;
        }
        if (parsingResult.getResultType() == ParsingResult.ResultType.SUCCESS) {
            context.get().execute(event);
        }

    }
}
