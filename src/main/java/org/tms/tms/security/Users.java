package org.tms.tms.security;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.management.relation.Role;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String username;

    @Column
    private String fullName;

    @Column
    @Email
    private String email;

    private String password;

    private String[] roles;

}
