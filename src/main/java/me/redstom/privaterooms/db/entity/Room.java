package me.redstom.privaterooms.db.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.VoiceChannel;

@Getter
@Setter

@Builder
public class Room {

    private VoiceChannel channel;
    private Template template;

}
