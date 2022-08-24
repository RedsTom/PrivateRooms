package me.redstom.privaterooms.db.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "roles")

@Getter
@Setter

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private long discordId;

    @Transient
    private net.dv8tion.jda.api.entities.Role discordRole;

    @ManyToOne
    private Guild guild;

}
