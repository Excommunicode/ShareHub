package ru.practicum.shareit.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.Email;

import static ru.practicum.shareit.constant.UserConstant.REGEX_EMAIL;


@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, name = "name")
    String name;
    @Email(regexp = REGEX_EMAIL, message = "не коректный адрес пользователя")
    @Column(nullable = false, name = "email", unique = true)
    String email;
}
