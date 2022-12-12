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

@Entity
@Table(name = "model_role")

@Getter
@Setter

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelRole implements ModelEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private me.redstom.privaterooms.entities.entity.Role referringRole;

    @Column
    private ModelEntityType type;

    @Override
    public String toString() {
        String sb =
                "ModelRole{" + "id=" + id + ", referringRole=" + referringRole + ", type=" + type
                        + '}';
        return sb;
    }
}
