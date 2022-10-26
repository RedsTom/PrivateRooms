//TODO add copyright

package me.redstom.privaterooms.entities.entity.model;

import lombok.RequiredArgsConstructor;
import me.redstom.privaterooms.entities.entity.Room;
import me.redstom.privaterooms.entities.entity.User;
import me.redstom.privaterooms.util.permission.PermissionSet;
import me.redstom.privaterooms.util.room.RoomVisibility;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.channel.concrete.VoiceChannelManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Service
@RequiredArgsConstructor
public class ModelSynchronizerService {

    private Function<Model, Model> update;
    private Room room;
    private VoiceChannelManager voiceChannelManager;

    private List<String> actions;

    public ModelSynchronizerService(Room room) {
        this.update = UnaryOperator.identity();
        this.room = room;
        this.voiceChannelManager = room.discordChannel().getManager();

        this.actions = new ArrayList<>();
    }

    public Room room() {
        return room;
    }

    public boolean actionsIsEmpty() {
        return actions.isEmpty();
    }

    public List<String> actions() {
        return Collections.unmodifiableList(actions);
    }

    public ModelSynchronizerService channelName(String channelName) {
        update = update.andThen(model -> model.channelName(channelName));
        voiceChannelManager.setName("%s %s".formatted(room.model().visibility().emoji(), channelName));
        actions.add("Set channel name to %s".formatted(channelName));
        return this;
    }

    public ModelSynchronizerService userLimit(int userLimit) {
        update = update.andThen(model -> model.maxUsers(userLimit));
        voiceChannelManager.setUserLimit(userLimit);
        actions.add("Set user limit to %s".formatted(userLimit));
        return this;
    }

    public ModelSynchronizerService visibility(RoomVisibility visibility) {
        VoiceChannel channel = voiceChannelManager.getChannel();
        update = update.andThen(model -> model.visibility(visibility));

        voiceChannelManager.putPermissionOverride(
          channel.getGuild().getPublicRole(),
          visibility.allowRaw(),
          visibility.denyRaw()
        );

        actions.add("Set channel visibility to %s".formatted(visibility.name()));
        channelName(room.model().channelName());
        return this;
    }

    public ModelSynchronizerService addUserTo(User user, ModelEntityType modelEntityType) {
        return switch (modelEntityType) {
            case WHITELIST -> addUserToWhitelist(user);
            case BLACKLIST -> addUserToBlacklist(user);
            case MODERATOR -> addUserToModerator(user);
        };
    }

    public ModelSynchronizerService addUserToWhitelist(User user) {
        Member member = voiceChannelManager.getGuild().retrieveMemberById(user.discordId()).complete();

        addUserToModel(user, ModelEntityType.WHITELIST);
        putPermissionTo(member, ModelEntityType.WHITELIST);
        actions.add("Added %s (%s) to the whitelist".formatted(member.getEffectiveName(), member.getId()));
        return this;
    }

    public ModelSynchronizerService addUserToBlacklist(User user) {
        Member member = voiceChannelManager.getGuild().retrieveMemberById(user.discordId()).complete();

        addUserToModel(user, ModelEntityType.BLACKLIST);
        putPermissionTo(member, ModelEntityType.BLACKLIST);
        actions.add("Added %s (%s) to the blacklist".formatted(member.getEffectiveName(), member.getId()));
        return this;
    }

    public ModelSynchronizerService addUserToModerator(User user) {
        Member member = voiceChannelManager.getGuild().retrieveMemberById(user.discordId()).complete();

        addUserToModel(user, ModelEntityType.MODERATOR);
        putPermissionTo(member, ModelEntityType.MODERATOR);
        actions.add("Added %s (%s) as a moderator".formatted(member.getEffectiveName(), member.getId()));
        return this;
    }

    private void addUserToModel(User user, ModelEntityType modelEntityType) {
        update = update.andThen(model -> addUserToModel(model, ModelUser.builder()
          .referringUser(user)
          .type(modelEntityType)
          .build())
        );
    }

    private Model addUserToModel(Model model, ModelUser modelUser) {
        model.users().add(modelUser);
        return model;
    }

    private void putPermissionTo(IPermissionHolder permissionHolder, PermissionSet permissionSet) {
        voiceChannelManager.putPermissionOverride(
          permissionHolder,
          permissionSet.allowRaw(),
          permissionSet.denyRaw()
        );
    }

    public ModelSynchronizerService reason(String reason) {
        voiceChannelManager.reason(reason);
        return this;
    }

    public CompletableFuture<Room> queue() {
        CompletableFuture<Room> future = new CompletableFuture<>();
        voiceChannelManager.queue(unused -> {
            update.apply(room.model());
            future.complete(room);
        });
        return future;
    }
}
