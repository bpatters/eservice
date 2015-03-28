package com.myl.eservice.web.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.myl.eservice.model.user.IUser;

/**
 * Created by bpatterson on 2/5/15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private String ID;
    private String email;
    // never serialize the password anywhere
    @JsonSerialize(using = NullSerializer.class)
    private String password;

    public UserDTO() {

    }

    public UserDTO(IUser user) {
        this.setID(user.getId())
            .setEmail(user.getEmail());
    }

    public void copyToModel(IUser user) {
        user.setEmail(this.getEmail())
            .setId(user.getId());
    }

    public String getID() {
        return ID;
    }

    public UserDTO setID(String ID) {
        this.ID = ID;

        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserDTO setEmail(String email) {
        this.email = email;

        return this;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
