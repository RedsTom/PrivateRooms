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

    private long discordId;

    @ManyToOne
    private Guild guild;

}
