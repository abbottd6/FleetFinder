package entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="planet_moon_system")
@Data
public class PlanetMoonSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="planet_id")
    private int planetId;

    @Column(name="planet_name")
    private String planetName;

    @Column(name="system_id")
    private int systemId;
}
