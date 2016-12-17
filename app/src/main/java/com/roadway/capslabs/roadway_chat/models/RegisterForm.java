package com.roadway.capslabs.roadway_chat.models;

/**
 * Created by kirill on 25.09.16
 */
public class RegisterForm {
    private final String email;
    private final String password1;
    private final String password2;

    public RegisterForm(String email, String password1, String password2) {
        this.email = email;
        this.password1 = password1;
        this.password2 = password2;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword1() {
        return password1;
    }

    public String getPassword2() {
        return password2;
    }

    @Override
    public String toString() {
        return getEmail() + " " + getPassword1() + " " + getPassword2();
    }
}
