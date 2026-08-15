package com.sc_fleetfinder.fleets.entities.GroupManagement;

import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.utils.GroupManagement.CrewTemplateCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Table(name = "crew_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrewTemplate {

    public CrewTemplate(String templateLabel, CrewTemplateCategory category, Users user) {
        this.templateLabel = templateLabel;
        this.templateCategory = category;
        this.owner = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_template")
    private Long templateId;

    @Column(name = "template_label", columnDefinition = "VARCHAR(64) NOT NULL")
    private String templateLabel;

    @Enumerated(EnumType.STRING)
    @Column(name = "template_category")
    private CrewTemplateCategory templateCategory;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Users owner;

    @Column(name = "last_used_at", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    @UpdateTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant lastUsedAt;
}
