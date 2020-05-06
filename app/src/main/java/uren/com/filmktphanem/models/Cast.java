package uren.com.filmktphanem.models;

/**
 * Created by Bram on 21/03/2018.
 */

/**
 * Represents a single cast member of a movie.
 */
public class Cast extends Person {
    private String character;
    private int castId;
    private String creditId;

    public Cast() {
    }

    /**
     * Initialize a cast member.
     *
     * @param name the cast name
     * @param character the cast character
     * @param profilePath the cast poster url
     */
    public Cast(String name, String character, String profilePath, int castId, String creditId, int id) {
        this.name = name;
        this.character = character;
        this.profilePath = profilePath;
        this.castId = castId;
        this.id = id;
        this.creditId = creditId;
        setProfileUrl(profilePath);
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }

    public int getCastId() {
        return castId;
    }

    public void setCastId(int castId) {
        this.castId = castId;
    }

}
