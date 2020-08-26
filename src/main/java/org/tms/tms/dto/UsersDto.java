package org.tms.tms.dto;

import lombok.Data;
import org.tms.tms.security.dao.Users;

@Data
public class UsersDto {

    private String email;
    private String fullName;
    private String pass;
    private String repeatPass;
    private String[] roles;
}
