package me.redstom.privaterooms.db.entity;

import lombok.*;
import me.redstom.privaterooms.util.room.RoomVisibility;

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
    private String channelName;

    @Column(nullable = false)
    private int maxUsers;

    @Column(nullable = false)
    private RoomVisibility visibility;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "template_whitelisted_users")
    @Singular
    private List<User> whitelistUsers;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "template_blacklisted_users")
    @Singular
    private List<User> blacklistUsers;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "template_moderator_users")
    @Singular
    private List<User> moderatorUsers;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "template_whitelisted_roles")
    @Singular
    private List<Role> whitelistRoles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "template_blacklisted_roles")
    @Singular
    private List<Role> blacklistRoles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "template_moderator_roles")
    @Singular
    private List<Role> moderatorRoles;
}
