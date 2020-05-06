package uren.com.filmktphanem.models;


import java.util.List;

public class User {

    private String userid = null;
    private String name = null;
    private String email = null;
    private String profilePhotoUrl = null;
    private boolean admin;
    private String loginMethod;

    public User() {
    }

    public User(String userid, String name, String email, String profilePhotoUrl,
               boolean admin, String loginMethod) {
        this.userid = userid;
        this.name = name;
        this.email = email;
        this.profilePhotoUrl = profilePhotoUrl;
        this.admin = admin;
        this.loginMethod = loginMethod;
    }

    public User(String userid) {
        this.userid = userid;
    }

    public User(String userid, String name, String profilePhotoUrl) {
        this.userid = userid;
        this.name = name;
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePhotoUrl() {
        return this.profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(String loginMethod) {
        this.loginMethod = loginMethod;
    }
}
