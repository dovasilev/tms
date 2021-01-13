package org.tms.tms.dto;

import lombok.Data;
import org.tms.tms.security.dao.Users;

@Data
public class UsersDto {

    public UsersDto(Users users) {
        this.email = users.getUserEmail();
        this.fullName = users.getFullName();
        this.pass = users.getPassword();
        this.roles = users.getRoles();
    }

    public UsersDto() {
    }


    private String email;
    private String fullName;
    private String pass;
    private String repeatPass;
    private String[] roles;
}
