package org.tms.tms.dto;

import lombok.Data;

@Data
public class SignUpField {

    private String email;
    private String fullName;
    private String login;
    private String pass;
    private String repeatPass;
}
