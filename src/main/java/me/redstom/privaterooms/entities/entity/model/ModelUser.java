//TODO add copyright

package me.redstom.privaterooms.entities.entity.model;

import lombok.*;
import me.redstom.privaterooms.entities.entity.User;

import javax.persistence.*;

@Entity
@Table(name = "model_user")

@Getter
@Setter

@Builder
@AllArgsConstructor
@NoArgsConstructor
public
class ModelUser implements ModelEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User referringUser;

    @Column
    private ModelEntityType type;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ModelUser{");
        sb.append("id=").append(id);
        sb.append(", referringUser=").append(referringUser);
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
