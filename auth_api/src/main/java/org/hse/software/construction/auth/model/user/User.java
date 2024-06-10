package org.hse.software.construction.auth.model.user;

import jakarta.persistence.*;
import lombok.*;
import org.hse.software.construction.auth.model.token.Token;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @Getter
  @Column(length = 50, nullable = false)
  private String nickname;
  @Column(length = 100, nullable = false, unique = true)
  private String email;
  @Column(nullable = false)
  private String password;
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Date created;
  @Enumerated(EnumType.STRING)
  private Role role;

  @OneToMany(mappedBy = "user")
  private List<Token> tokens;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public User(int id, String nickname, String email, String password, Date created, Role role) {
    this.id = id;
    this.nickname = nickname;
    this.email = email;
    this.password = password;
    this.created = created;
    this.role = role;
  }
}
