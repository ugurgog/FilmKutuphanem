package uren.com.filmktphanem.models;

/**
 * Created by Bram on 21/03/2018.
 */

/**
 * Initialize a single crew member of a movie.
 */
public class Crew extends Person {
    private String job;
    private String creditId;

    public Crew() {
    }

    /**
     * Initialize a crew member.
     * @param name the crew name
     * @param job the crew job
     * @param profilePath the crew poster url
     */
    public Crew(String name, String job, String profilePath, String creditId, int id) {
        this.name = name;
        this.id = id;
        this.job = job;
        this.profilePath = profilePath;
        this.creditId = creditId;
        setProfileUrl(profilePath);
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }

}
