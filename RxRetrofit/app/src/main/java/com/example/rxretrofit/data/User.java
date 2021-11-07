package com.example.rxretrofit.data;

public class User {
    String login;
    int  id;

    String   avatar_url;
    String url;

    public String getName() {
        return url;
    }

    public void setName(String name) {
        this.url = name;
    }

    public User() {

    }
    public User(String login, int id, String avatar_url,String name) {
        this.login = login;
        this.id = id;
        this.avatar_url = avatar_url;
        this.url =name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
}
