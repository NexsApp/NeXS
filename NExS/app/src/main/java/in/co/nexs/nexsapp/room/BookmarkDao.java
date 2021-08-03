package in.co.nexs.nexsapp.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

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
