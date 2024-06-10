package org.hse.software.construction.auth.model.session;

import jakarta.persistence.*;
import lombok.Getter;
import org.hse.software.construction.auth.model.user.User;
import java.util.Date;

@Getter
@Entity
@Table(name = "session")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Date expires;

    public void setUser(User user) {
        this.user = user;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }
}
