package com.example.nexs.room;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {BookmarkedArticle.class, LikedArticle.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BookmarkDao getBookmarkDao();

    public abstract LikeDao getLikeDao();

    private static AppDatabase db;

    public static AppDatabase getAppDatabase(Application application) {
        if (db == null) {
            db = Room.databaseBuilder(application.getApplicationContext(), AppDatabase.class, "nexs-local-storage")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return db;
    }
}
