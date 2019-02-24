package uren.com.filmktphanem.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "favorites.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold favorites data
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoritesContract.FavoritesEntry.TABLE_NAME + " (" +
                FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                FavoritesContract.FavoritesEntry.COLUMN_MOVIE_NAME + " STRING NOT NULL, " +
                FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH + " STRING NOT NULL, " +
                FavoritesContract.FavoritesEntry.COLUMN_POSTER_SMALL + " STRING, " +
                FavoritesContract.FavoritesEntry.COLUMN_POSTER_LARGE + " STRING, " +
                FavoritesContract.FavoritesEntry.COLUMN_BACK_DROP_LARGE + " STRING, " +
                FavoritesContract.FavoritesEntry.COLUMN_WATCHED + "  INTEGER DEFAULT 0," +
                FavoritesContract.FavoritesEntry.COLUMN_WILL_WATCH+ "  INTEGER DEFAULT 0," +
                FavoritesContract.FavoritesEntry.COLUMN_MY_RATE + "  REAL DEFAULT 0," +
                FavoritesContract.FavoritesEntry.COLUMN_IN_FAVORITES + "  INTEGER DEFAULT 0," +
                FavoritesContract.FavoritesEntry.COLUMN_MY_COMMENT + " STRING , " +
                FavoritesContract.FavoritesEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoritesContract.FavoritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addToMyLibrary(MyLibraryItem myLibraryItem) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID, myLibraryItem.getMovieId());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_NAME, myLibraryItem.getName());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH, myLibraryItem.getPosterPath());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_POSTER_SMALL, myLibraryItem.getPosterSmall());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_POSTER_LARGE, myLibraryItem.getPosterLarge());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_BACK_DROP_LARGE, myLibraryItem.getBackDropLarge());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_WATCHED, myLibraryItem.getWatched());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_WILL_WATCH, myLibraryItem.getWillWatch());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_MY_RATE, myLibraryItem.getMyRate());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_MY_COMMENT, myLibraryItem.getMyComment());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_IN_FAVORITES, myLibraryItem.getInFavorites());
        sqLiteDatabase.insert(FavoritesContract.FavoritesEntry.TABLE_NAME, null, values);
        sqLiteDatabase.close();
    }

    public int updateLibraryItem(MyLibraryItem myLibraryItem) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FavoritesContract.FavoritesEntry.COLUMN_WATCHED, myLibraryItem.getWatched());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_WILL_WATCH, myLibraryItem.getWillWatch());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_MY_RATE, myLibraryItem.getMyRate());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_MY_COMMENT, myLibraryItem.getMyComment());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_IN_FAVORITES, myLibraryItem.getInFavorites());

        return sqLiteDatabase.update(FavoritesContract.FavoritesEntry.TABLE_NAME, values,
                FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + " =?",
                new String[]{String.valueOf(myLibraryItem.getMovieId())});
    }

    public void deleteLibraryItem(MyLibraryItem myLibraryItem) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(FavoritesContract.FavoritesEntry.TABLE_NAME,
                FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + " =?",
                new String[]{String.valueOf(myLibraryItem.getMovieId())});
        database.close();
    }

    public MyLibraryItem getLibraryItem(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(FavoritesContract.FavoritesEntry.TABLE_NAME,
                new String[]{FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID,
                        FavoritesContract.FavoritesEntry.COLUMN_MOVIE_NAME,
                        FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH,
                        FavoritesContract.FavoritesEntry.COLUMN_POSTER_SMALL,
                        FavoritesContract.FavoritesEntry.COLUMN_POSTER_LARGE,
                        FavoritesContract.FavoritesEntry.COLUMN_BACK_DROP_LARGE,
                        FavoritesContract.FavoritesEntry.COLUMN_WATCHED,
                        FavoritesContract.FavoritesEntry.COLUMN_WILL_WATCH,
                        FavoritesContract.FavoritesEntry.COLUMN_MY_RATE,
                        FavoritesContract.FavoritesEntry.COLUMN_MY_COMMENT,
                        FavoritesContract.FavoritesEntry.COLUMN_IN_FAVORITES},
                FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + " =?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return new MyLibraryItem(cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(6),
                cursor.getInt(7),
                cursor.getFloat(8),
                cursor.getString(9),
                cursor.getInt(10),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5));
    }

    public List<MyLibraryItem> getAllLibraryItems(){
        List<MyLibraryItem> myLibraryItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + FavoritesContract.FavoritesEntry.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            do{
                MyLibraryItem myLibraryItem = new MyLibraryItem();
                myLibraryItem.setMovieId(cursor.getInt(0));
                myLibraryItem.setName(cursor.getString(1));
                myLibraryItem.setPosterPath(cursor.getString(2));

                myLibraryItem.setPosterSmall(cursor.getString(3));
                myLibraryItem.setPosterLarge(cursor.getString(4));
                myLibraryItem.setBackDropLarge(cursor.getString(5));

                myLibraryItem.setWatched(cursor.getInt(6));
                myLibraryItem.setWillWatch(cursor.getInt(7));
                myLibraryItem.setMyRate(cursor.getFloat(8));
                myLibraryItem.setMyComment(cursor.getString(9));
                myLibraryItem.setInFavorites(cursor.getInt(10));
                myLibraryItems.add(myLibraryItem);
            }
            while (cursor.moveToNext());
        }
        return myLibraryItems;
    }

    public List<MyLibraryItem> getWatchedLibraryItems(int watchedValue){
        List<MyLibraryItem> myLibraryItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(FavoritesContract.FavoritesEntry.TABLE_NAME,
                new String[]{FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID,
                        FavoritesContract.FavoritesEntry.COLUMN_MOVIE_NAME,
                        FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH,
                        FavoritesContract.FavoritesEntry.COLUMN_POSTER_SMALL,
                        FavoritesContract.FavoritesEntry.COLUMN_POSTER_LARGE,
                        FavoritesContract.FavoritesEntry.COLUMN_BACK_DROP_LARGE,
                        FavoritesContract.FavoritesEntry.COLUMN_WATCHED,
                        FavoritesContract.FavoritesEntry.COLUMN_WILL_WATCH,
                        FavoritesContract.FavoritesEntry.COLUMN_MY_RATE,
                        FavoritesContract.FavoritesEntry.COLUMN_MY_COMMENT,
                        FavoritesContract.FavoritesEntry.COLUMN_IN_FAVORITES},
                FavoritesContract.FavoritesEntry.COLUMN_WATCHED + " =?",
                new String[]{String.valueOf(watchedValue)}, null, null, null, null);

        if(cursor.moveToFirst()){
            do{
                MyLibraryItem myLibraryItem = new MyLibraryItem();
                myLibraryItem.setMovieId(cursor.getInt(0));
                myLibraryItem.setName(cursor.getString(1));
                myLibraryItem.setPosterPath(cursor.getString(2));
                myLibraryItem.setPosterSmall(cursor.getString(3));
                myLibraryItem.setPosterLarge(cursor.getString(4));
                myLibraryItem.setBackDropLarge(cursor.getString(5));
                myLibraryItem.setWatched(cursor.getInt(6));
                myLibraryItem.setWillWatch(cursor.getInt(7));
                myLibraryItem.setMyRate(cursor.getFloat(8));
                myLibraryItem.setMyComment(cursor.getString(9));
                myLibraryItem.setInFavorites(cursor.getInt(10));
                myLibraryItems.add(myLibraryItem);
            }
            while (cursor.moveToNext());
        }
        return myLibraryItems;
    }

    public List<MyLibraryItem> getWillWatchLibraryItems(int willWatch){
        List<MyLibraryItem> myLibraryItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(FavoritesContract.FavoritesEntry.TABLE_NAME,
                new String[]{FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID,
                        FavoritesContract.FavoritesEntry.COLUMN_MOVIE_NAME,
                        FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH,
                        FavoritesContract.FavoritesEntry.COLUMN_POSTER_SMALL,
                        FavoritesContract.FavoritesEntry.COLUMN_POSTER_LARGE,
                        FavoritesContract.FavoritesEntry.COLUMN_BACK_DROP_LARGE,
                        FavoritesContract.FavoritesEntry.COLUMN_WATCHED,
                        FavoritesContract.FavoritesEntry.COLUMN_WILL_WATCH,
                        FavoritesContract.FavoritesEntry.COLUMN_MY_RATE,
                        FavoritesContract.FavoritesEntry.COLUMN_MY_COMMENT,
                        FavoritesContract.FavoritesEntry.COLUMN_IN_FAVORITES},
                FavoritesContract.FavoritesEntry.COLUMN_WILL_WATCH + " =?",
                new String[]{String.valueOf(willWatch)}, null, null, null, null);

        if(cursor.moveToFirst()){
            do{
                MyLibraryItem myLibraryItem = new MyLibraryItem();
                myLibraryItem.setMovieId(cursor.getInt(0));
                myLibraryItem.setName(cursor.getString(1));
                myLibraryItem.setPosterPath(cursor.getString(2));
                myLibraryItem.setPosterSmall(cursor.getString(3));
                myLibraryItem.setPosterLarge(cursor.getString(4));
                myLibraryItem.setBackDropLarge(cursor.getString(5));
                myLibraryItem.setWatched(cursor.getInt(6));
                myLibraryItem.setWillWatch(cursor.getInt(7));
                myLibraryItem.setMyRate(cursor.getFloat(8));
                myLibraryItem.setMyComment(cursor.getString(9));
                myLibraryItem.setInFavorites(cursor.getInt(10));
                myLibraryItems.add(myLibraryItem);
            }
            while (cursor.moveToNext());
        }
        return myLibraryItems;
    }

    public List<MyLibraryItem> getFavoritesItems(int favoriteValue){
        List<MyLibraryItem> myLibraryItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(FavoritesContract.FavoritesEntry.TABLE_NAME,
                new String[]{FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID,
                        FavoritesContract.FavoritesEntry.COLUMN_MOVIE_NAME,
                        FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH,
                        FavoritesContract.FavoritesEntry.COLUMN_POSTER_SMALL,
                        FavoritesContract.FavoritesEntry.COLUMN_POSTER_LARGE,
                        FavoritesContract.FavoritesEntry.COLUMN_BACK_DROP_LARGE,
                        FavoritesContract.FavoritesEntry.COLUMN_WATCHED,
                        FavoritesContract.FavoritesEntry.COLUMN_WILL_WATCH,
                        FavoritesContract.FavoritesEntry.COLUMN_MY_RATE,
                        FavoritesContract.FavoritesEntry.COLUMN_MY_COMMENT,
                        FavoritesContract.FavoritesEntry.COLUMN_IN_FAVORITES},
                FavoritesContract.FavoritesEntry.COLUMN_WATCHED + " =?",
                new String[]{String.valueOf(favoriteValue)}, null, null, null, null);

        if(cursor.moveToFirst()){
            do{
                MyLibraryItem myLibraryItem = new MyLibraryItem();
                myLibraryItem.setMovieId(cursor.getInt(0));
                myLibraryItem.setName(cursor.getString(1));
                myLibraryItem.setPosterPath(cursor.getString(2));
                myLibraryItem.setPosterSmall(cursor.getString(3));
                myLibraryItem.setPosterLarge(cursor.getString(4));
                myLibraryItem.setBackDropLarge(cursor.getString(5));
                myLibraryItem.setWatched(cursor.getInt(6));
                myLibraryItem.setWillWatch(cursor.getInt(7));
                myLibraryItem.setMyRate(cursor.getFloat(8));
                myLibraryItem.setMyComment(cursor.getString(9));
                myLibraryItem.setInFavorites(cursor.getInt(10));
                myLibraryItems.add(myLibraryItem);
            }
            while (cursor.moveToNext());
        }
        return myLibraryItems;
    }

    public List<MyLibraryItem> getItemsByRate(String orderByType){
        List<MyLibraryItem> myLibraryItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(FavoritesContract.FavoritesEntry.TABLE_NAME,
                new String[]{FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID,
                        FavoritesContract.FavoritesEntry.COLUMN_MOVIE_NAME,
                        FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH,
                        FavoritesContract.FavoritesEntry.COLUMN_POSTER_SMALL,
                        FavoritesContract.FavoritesEntry.COLUMN_POSTER_LARGE,
                        FavoritesContract.FavoritesEntry.COLUMN_BACK_DROP_LARGE,
                        FavoritesContract.FavoritesEntry.COLUMN_WATCHED,
                        FavoritesContract.FavoritesEntry.COLUMN_WILL_WATCH,
                        FavoritesContract.FavoritesEntry.COLUMN_MY_RATE,
                        FavoritesContract.FavoritesEntry.COLUMN_MY_COMMENT,
                        FavoritesContract.FavoritesEntry.COLUMN_IN_FAVORITES},
                null,null, null, null, orderByType, null);

        if(cursor.moveToFirst()){
            do{
                MyLibraryItem myLibraryItem = new MyLibraryItem();
                myLibraryItem.setMovieId(cursor.getInt(0));
                myLibraryItem.setName(cursor.getString(1));
                myLibraryItem.setPosterPath(cursor.getString(2));
                myLibraryItem.setPosterSmall(cursor.getString(3));
                myLibraryItem.setPosterLarge(cursor.getString(4));
                myLibraryItem.setBackDropLarge(cursor.getString(5));
                myLibraryItem.setWatched(cursor.getInt(6));
                myLibraryItem.setWillWatch(cursor.getInt(7));
                myLibraryItem.setMyRate(cursor.getFloat(8));
                myLibraryItem.setMyComment(cursor.getString(9));
                myLibraryItem.setInFavorites(cursor.getInt(10));
                myLibraryItems.add(myLibraryItem);
            }
            while (cursor.moveToNext());
        }
        return myLibraryItems;
    }

    public Boolean isInMyLibrary(int movieId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.rawQuery(
                "SELECT * FROM " + FavoritesContract.FavoritesEntry.TABLE_NAME +
                        " WHERE " + FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + "= " + movieId
                , null);

        return mCursor.moveToFirst();
    }

}