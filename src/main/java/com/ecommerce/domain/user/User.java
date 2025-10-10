package com.ecommerce.domain.user;

import com.ecommerce.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users")
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @Column(nullable = false, length = 255)
  private String password;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 20)
  private String phone;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Role role = Role.USER;

  public static User createUser(String email, String password, String name, String phone) {
    User user = new User();
    user.email = email;
    user.password = password;
    user.name = name;
    user.phone = phone;
    user.role = Role.USER;
    return user;
  }

}
