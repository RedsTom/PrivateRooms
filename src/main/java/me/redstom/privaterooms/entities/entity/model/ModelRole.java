//TODO add copyright

package me.redstom.privaterooms.entities.entity.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "model_role")

@Getter
@Setter

@Builder
@AllArgsConstructor
@NoArgsConstructor
public
class ModelRole implements ModelEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private me.redstom.privaterooms.entities.entity.Role referringRole;

    @Column
    private ModelEntityType type;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ModelRole{");
        sb.append("id=").append(id);
        sb.append(", referringRole=").append(referringRole);
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
