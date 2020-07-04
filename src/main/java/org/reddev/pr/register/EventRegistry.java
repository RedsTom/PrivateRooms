package org.reddev.pr.register;

import fr.il_totore.ucp.registration.CommandRegistry;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;
import org.reddev.pr.event.*;

public class EventRegistry {
    public EventRegistry(DiscordApi api, CommandRegistry<MessageCreateEvent> registry) {

        api.addMessageCreateListener(new MessageCreateEventListener(registry));
        api.addServerJoinListener(new ServerJoinEventListener());
        api.addServerLeaveListener(new ServerLeaveEventListener());
        api.addServerVoiceChannelMemberJoinListener(new ServerVoiceChannelMemberJoinEventListener());
        api.addServerVoiceChannelMemberLeaveListener(new ServerVoiceChannelMemberLeaveEventListener());

    }
}
