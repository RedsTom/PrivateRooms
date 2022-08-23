package me.redstom.privaterooms.db.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "guilds")

@Getter
@Setter

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Guild {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private long discordId;

    @Transient
    private net.dv8tion.jda.api.entities.Guild discordGuild;

    private long categoryId;
    private long createChannelId;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<User> users;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Role> roles;
}
