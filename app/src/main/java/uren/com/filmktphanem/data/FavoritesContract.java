package uren.com.filmktphanem.data;

import android.provider.BaseColumns;


/**
 * Favorites contract.
 * This class is used to define how the favorites table in the SQL local storage will look like.
 */
public class FavoritesContract {

    /**
     * The properties of the favorites table defined.
     */
    public static final class FavoritesEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_NAME = "name";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_WATCHED = "watched";
        public static final String COLUMN_WILL_WATCH = "willwatch";
        public static final String COLUMN_MY_RATE = "myrate";
        public static final String COLUMN_IN_FAVORITES = "infavorites";
        public static final String COLUMN_MY_COMMENT = "mycomment";
        public static final String COLUMN_POSTER_SMALL = "postersmall";
        public static final String COLUMN_POSTER_LARGE = "posterlarge";
        public static final String COLUMN_BACK_DROP_LARGE = "backdroplarge";
    }

}
