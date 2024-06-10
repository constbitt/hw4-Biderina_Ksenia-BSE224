package org.hse.software.construction.tickets.model.user;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User {
    @Id
    private Integer id;
    @Getter
    @Column(length = 50, nullable = false)
    private String nickname;
    @Column(length = 100, nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
}
