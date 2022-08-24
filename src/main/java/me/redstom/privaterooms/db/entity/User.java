package me.redstom.privaterooms.db.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")

@Getter
@Setter

@Builder
@AllArgsConstructor
@NoArgsConstructor

public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private long discordId;

    @Transient
    private net.dv8tion.jda.api.entities.User discordUser;

    @OneToMany
    private List<Template> templates;

    @Override
    public String toString() {
        return "User(id=%s, discordId=%s)".formatted(id, discordId);
    }
}
