package com.myl.eservice.web.dto.user;

/**
 * Created by bpatterson on 2/21/15.
 */
public class LoginDTO {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
