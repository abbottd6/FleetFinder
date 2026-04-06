package com.sc_fleetfinder.fleets.entities.GroupManagement;

import com.sc_fleetfinder.fleets.entities.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "crew_role_classification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrewRoleClassification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private Long roleId;

    @Column(name = "role_category", columnDefinition = "VARCHAR(32) NOT NULL")
    private String roleCategory;

    @Column(name = "role_title", columnDefinition = "VARCHAR(32) NOT NULL")
    private String roleTitle;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
