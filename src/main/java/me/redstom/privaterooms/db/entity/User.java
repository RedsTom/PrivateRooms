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

    private long discordId;

    @OneToMany
    private List<Template> templates;

    @Override
    public String toString() {
        return "User(id=%s, discordId=%s)".formatted(id, discordId);
    }
}
