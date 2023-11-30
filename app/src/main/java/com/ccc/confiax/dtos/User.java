package com.ccc.confiax.dtos;

public class User {

    private Id idUser;
    private String name;
    private String email;
    private String password;
    private String birthday;

    public User() {
        this.idUser = idUser;
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
    }

    public Id getIdUser() {
        return idUser;
    }

    public void setIdUser(Id idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

}
