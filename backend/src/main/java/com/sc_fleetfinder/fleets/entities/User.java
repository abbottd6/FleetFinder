package com.sc_fleetfinder.fleets.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
import org.apache.catalina.Server;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="user")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_user")
    @NotNull(message = "UserId cannot be null")
    private Long userId;

    @Column(name="user_name")
    @Size(min = 1, max = 32, message = "Username must be between 1 and 32 characters in length")
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @Column(name="user_password")
    @Size(min = 8, max = 32, message = "User password must be between 8 and 32 characters in length")
    @NotBlank(message = "User password cannot be blank")
    private String password;

    @Column(name="email")
    @NotBlank(message = "User email cannot be blank")
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

    @OneToMany(cascade = CascadeType.ALL, mappedBy="user", fetch = FetchType.EAGER)
    private Set<GroupListing> groupListings = new HashSet<>();
}
