package com.sc_fleetfinder.fleets.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.ServerRegion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users")
@Getter
@Setter
public class Users {

    // ##TODO Change userId type to UUID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_user")
    private Long userId;

    @Column(name="keycloak_id", columnDefinition = "CHAR(36)", length = 36)
    @JdbcTypeCode(Types.VARCHAR)
    @NotNull(message = "keycloak_id cannot be null")
    private String keycloakId;

    @Column(name="user_name")
    @Size(min = 1, max = 32, message = "Username must be between 1 and 32 characters in length")
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @Column(name="email")
    @NotBlank(message = "Users email cannot be blank")
    @Email
    private String email;

    @ManyToOne
    @JoinColumn(name="server_id")
    private ServerRegion serverId;

    @Column(name="org")
    private String org;

    @Column(name="about_user")
    private String about;

    @Column(name="acct_created")
    @CreationTimestamp
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private LocalDateTime acctCreated;

    @Column(name="last_login")
    @UpdateTimestamp
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private LocalDateTime lastLogin;

    @Column(name="is_deleted")
    private Boolean isDeleted;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, mappedBy="users")
    @JsonManagedReference
    private Set<GroupListing> groupListings = new HashSet<>();
}
