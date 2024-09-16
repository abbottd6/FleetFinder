package entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_user")
    private Long userId;

    @Column(name="user_name")
    private String username;

    @Column(name="user_password")
    private String password;

    @Column(name="email")
    private String email;

    @Column(name="server_id")
    private String serverId;

    @Column(name="org")
    private String org;

    @Column(name="about_user")
    private String about;

    @Column(name="acct_created")
    @CreationTimestamp
    private LocalDateTime acctCreated;
}
