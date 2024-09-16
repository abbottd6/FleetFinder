package entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="planetary_system")
@Data
public class PlanetarySystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="system_id")
    private int systemId;

    @Column(name="system_name")
    private String systemName;
}
