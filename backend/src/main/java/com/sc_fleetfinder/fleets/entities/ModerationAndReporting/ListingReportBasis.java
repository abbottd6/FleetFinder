package com.sc_fleetfinder.fleets.entities.ModerationAndReporting;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="listing_report_basis")
@Data
public class ListingReportBasis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_basis")
    private Integer basisId;

    @Column(name="basis_label")
    private String basisLabel;
}
