//TODO add copyright

package me.redstom.privaterooms.entities.entity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.redstom.privaterooms.entities.entity.User;

@Entity
@Table(name = "model_user")

@Getter
@Setter

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelUser implements ModelEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User referringUser;

    @Column
    private ModelEntityType type;

    @Override
    public String toString() {
        return "ModelUser{" + "id=" + id
                + ", referringUser=" + referringUser
                + ", type=" + type
                + '}';
    }
}
