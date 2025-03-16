package com.sc_fleetfinder.fleets.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="server_region")
@Data
public class ServerRegion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="server_id")
    private Integer serverId;

    @Column(name="server_name")
    private String serverName;
}
