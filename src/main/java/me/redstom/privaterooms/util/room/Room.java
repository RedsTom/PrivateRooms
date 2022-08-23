package me.redstom.privaterooms.util.room;

import lombok.Builder;
import lombok.Getter;
import me.redstom.privaterooms.db.entity.Template;
import net.dv8tion.jda.api.entities.VoiceChannel;

@Builder
public class Room {

    @Getter
    private Template descriptor;

    @Getter
    private VoiceChannel channel;

}
