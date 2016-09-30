package com.roadway.capslabs.roadway_chat.models;

/**
 * Created by kirill on 25.09.16
 */
public class User {
    private final String email;
    private final String firstName;
    private String lastName = "";
    private String userName = "";
    private final String password1;
    private final String password2;

    public User(String email, String firstName, String lastName, String userName, String password1, String password2) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password1 = password1;
        this.password2 = password2;
    }

    public User(String email, String firstName, String password1, String password2) {
        this.email = email;
        this.firstName = firstName;
        this.password1 = password1;
        this.password2 = password2;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword1() {
        return password1;
    }

    public String getPassword2() {
        return password2;
    }

    @Override
    public String toString() {
        return getEmail()+ " " + getFirstName() + " " + getLastName() + " "
                + getUserName() + " " + getPassword1() + " " + getPassword2();
    }
}
