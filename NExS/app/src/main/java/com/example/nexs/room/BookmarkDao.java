package com.example.nexs.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Set;

@Dao
public interface BookmarkDao {
    @Query("SELECT * FROM bookmarkedarticle")
    LiveData<List<BookmarkedArticle>> getBookmarks();

    @Query("SELECT id from BookmarkedArticle")
    LiveData<List<String>> getBookmarkIds();

    @Query("DELETE FROM bookmarkedarticle")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addBookmark(BookmarkedArticle article);

    @Query("UPDATE bookmarkedarticle SET likes = :val WHERE id = :id")
    void updateBookmark(Integer val, String id);

    @Delete
    void removeBookmark(BookmarkedArticle article);

}
