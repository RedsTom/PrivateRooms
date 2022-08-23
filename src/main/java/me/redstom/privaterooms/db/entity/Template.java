package me.redstom.privaterooms.db.entity;

import lombok.*;
import me.redstom.privaterooms.util.room.RoomState;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "templates")

@Getter
@Setter

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Template {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User author;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int maxUsers;

    @Column(nullable = false)
    private RoomState visibility;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "template_whitelisted_users")
    private List<User> whitelistUsers;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "template_blacklisted_users")
    private List<User> blacklistUsers;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "template_moderator_users")
    private List<User> moderatorUsers;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "template_whitelisted_roles")
    private List<Role> whitelistRoles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "template_blacklisted_roles")
    private List<Role> blacklistRoles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "template_moderator_roles")
    private List<Role> moderatorRoles;
}
