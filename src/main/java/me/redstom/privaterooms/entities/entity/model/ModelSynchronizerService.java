//TODO add copyright

package me.redstom.privaterooms.entities.entity.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.UnaryOperator;
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

@Service
@RequiredArgsConstructor
public class ModelSynchronizerService {

    private Function<Model, Model> update;
    private Model                  updatedModel;
    private Room                   room;
    private VoiceChannelManager    voiceChannelManager;

    private List<String> actions;

    public ModelSynchronizerService(Room room) {
        this.update = UnaryOperator.identity();
        this.updatedModel = Model.copyOf(room.model());
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
        UnaryOperator<Model> modelOperator = model -> model.channelName(channelName);

        if (updatedModelEquals(modelOperator)) {
            return this;
        }
        updatedModel = modelOperator.apply(updatedModel);

        update = update.andThen(modelOperator);
        voiceChannelManager.setName(
                "%s %s".formatted(room.model().visibility().emoji(), channelName));
        actions.add("Set channel name to %s".formatted(channelName));

        return this;
    }

    public ModelSynchronizerService userLimit(int userLimit) {
        UnaryOperator<Model> modelOperator = model -> model.userLimit(userLimit);

        if (updatedModelEquals(modelOperator)) {
            return this;
        }
        updatedModel = modelOperator.apply(updatedModel);

        update = update.andThen(modelOperator);
        voiceChannelManager.setUserLimit(userLimit);
        actions.add("Set user limit to %s".formatted(userLimit));

        return this;
    }

    public ModelSynchronizerService visibility(RoomVisibility visibility) {
        UnaryOperator<Model> modelOperator = model -> model.visibility(visibility);

        if (updatedModelEquals(modelOperator)) {
            return this;
        }
        updatedModel = modelOperator.apply(updatedModel);

        VoiceChannel channel = voiceChannelManager.getChannel();

        update = update.andThen(modelOperator);
        voiceChannelManager.putPermissionOverride(
                channel.getGuild().getPublicRole(),
                visibility.allowRaw(),
                visibility.denyRaw()
        );
        actions.add("Set channel visibility to %s".formatted(visibility.name()));

        channelName(updatedModel.channelName());

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
        Member member =
                voiceChannelManager.getGuild().retrieveMemberById(user.discordId()).complete();

        if (isUserInModel(user, ModelEntityType.WHITELIST)) {
            return this;
        }

        addUserToModel(user, ModelEntityType.WHITELIST);
        putPermissionTo(member, ModelEntityType.WHITELIST);
        actions.add("Added %s (%s) to the whitelist".formatted(member.getEffectiveName(),
                member.getId()));

        return this;
    }

    public ModelSynchronizerService addUserToBlacklist(User user) {
        Member member =
                voiceChannelManager.getGuild().retrieveMemberById(user.discordId()).complete();

        if (isUserInModel(user, ModelEntityType.BLACKLIST)) {
            return this;
        }

        addUserToModel(user, ModelEntityType.BLACKLIST);
        putPermissionTo(member, ModelEntityType.BLACKLIST);
        actions.add("Added %s (%s) to the blacklist".formatted(member.getEffectiveName(),
                member.getId()));

        return this;
    }

    public ModelSynchronizerService addUserToModerator(User user) {
        Member member =
                voiceChannelManager.getGuild().retrieveMemberById(user.discordId()).complete();

        if (isUserInModel(user, ModelEntityType.MODERATOR)) {
            return this;
        }

        addUserToModel(user, ModelEntityType.MODERATOR);
        putPermissionTo(member, ModelEntityType.MODERATOR);
        actions.add("Added %s (%s) as a moderator".formatted(member.getEffectiveName(),
                member.getId()));

        return this;
    }

    private boolean isUserInModel(User user, ModelEntityType modelEntityType) {
        //TODO Optimize this
        return updatedModel.users().stream()
                .filter(modelUser -> modelUser.type() == modelEntityType)
                .map(ModelUser::referringUser)
                .mapToLong(User::discordId)
                .anyMatch(discordId -> discordId == user.discordId());
    }

    private void addUserToModel(User user, ModelEntityType modelEntityType) {
        UnaryOperator<Model> modelOperator = model -> addUserToModel(model, ModelUser.builder()
                .referringUser(user)
                .type(modelEntityType)
                .build());

        updatedModel = modelOperator.apply(updatedModel);

        update = update.andThen(modelOperator);
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

    public ModelSynchronizerService apply(Model model) {
        channelName(model.channelName());
        userLimit(model.userLimit());
        visibility(model.visibility());
        model.users()
                .forEach(modelUser -> addUserTo(modelUser.referringUser(), modelUser.type()));

        return this;
    }

    private boolean updatedModelEquals(UnaryOperator<Model> modelUnaryOperator) {
        return updatedModel.equals(modelUnaryOperator.apply(Model.copyOf(updatedModel)));
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
