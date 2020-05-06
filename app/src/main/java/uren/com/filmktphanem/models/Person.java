package uren.com.filmktphanem.models;

/**
 * Created by bram on 25/03/18.
 */

public class Person {

    public String name;
    String profilePath;
    private String profileUrl;
    public String birthday;
    public String known_for_department;
    public String deathday;
    public int id;
    public int gender;
    public String biography;
    public String place_of_birth;
    public String imdb_id;
    public String homepage;

    private PersonExternalInfo externalInfo;

    private final static String TMDB_IMG_BASE_URL = "http://image.tmdb.org/t/p/";

    public Person() {
    }

    public String getName() {
        return name;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getKnown_for_department() {
        return known_for_department;
    }

    public void setKnown_for_department(String known_for_department) {
        this.known_for_department = known_for_department;
    }

    public String getDeathday() {
        return deathday;
    }

    public void setDeathday(String deathday) {
        this.deathday = deathday;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getPlace_of_birth() {
        return place_of_birth;
    }

    public void setPlace_of_birth(String place_of_birth) {
        this.place_of_birth = place_of_birth;
    }

    public String getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public void setProfileUrl(String profilePath) {
        profileUrl = TMDB_IMG_BASE_URL + "w300" + profilePath;
    }

    public PersonExternalInfo getExternalInfo() {
        return externalInfo;
    }

    public void setExternalInfo(PersonExternalInfo externalInfo) {
        this.externalInfo = externalInfo;
    }
}
