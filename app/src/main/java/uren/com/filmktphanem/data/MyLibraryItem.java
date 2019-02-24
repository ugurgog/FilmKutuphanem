package uren.com.filmktphanem.data;

public class MyLibraryItem {

    private int movieId;
    private String name;
    private String posterPath;
    private int watched;
    private int willWatch;
    private int myRate;
    private String myComment;
    private int inFavorites;
    private String posterSmall;
    private String posterLarge;
    private String backDropLarge;

    public MyLibraryItem(){

    }

    public MyLibraryItem(int movieId, String name, String posterPath, int watched, int willWatch, int myRate, String myComment,
                         int inFavorites, String posterSmall, String posterLarge, String backDropLarge) {
        this.movieId = movieId;
        this.name = name;
        this.posterPath = posterPath;
        this.watched = watched;
        this.willWatch = willWatch;
        this.myRate = myRate;
        this.myComment = myComment;
        this.inFavorites = inFavorites;
        this.posterSmall = posterSmall;
        this.posterLarge = posterLarge;
        this.backDropLarge = backDropLarge;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public int getWatched() {
        return watched;
    }

    public int getWillWatch() {
        return willWatch;
    }

    public void setWillWatch(int willWatch) {
        this.willWatch = willWatch;
    }

    public void setWatched(int watched) {
        this.watched = watched;
    }

    public int getMyRate() {
        return myRate;
    }

    public void setMyRate(int myRate) {
        this.myRate = myRate;
    }

    public String getMyComment() {
        return myComment;
    }

    public void setMyComment(String myComment) {
        this.myComment = myComment;
    }

    public int getInFavorites() {
        return inFavorites;
    }

    public void setInFavorites(int inFavorites) {
        this.inFavorites = inFavorites;
    }

    public String getPosterSmall() {
        return posterSmall;
    }

    public void setPosterSmall(String posterSmall) {
        this.posterSmall = posterSmall;
    }

    public String getPosterLarge() {
        return posterLarge;
    }

    public void setPosterLarge(String posterLarge) {
        this.posterLarge = posterLarge;
    }

    public String getBackDropLarge() {
        return backDropLarge;
    }

    public void setBackDropLarge(String backDropLarge) {
        this.backDropLarge = backDropLarge;
    }
}
