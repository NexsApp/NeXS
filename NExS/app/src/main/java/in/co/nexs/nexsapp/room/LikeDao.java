package in.co.nexs.nexsapp.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LikeDao {
    @Query("SELECT * FROM likedarticle")
    LiveData<List<LikedArticle>> getLikes();

    @Query("DELETE FROM likedarticle")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addLike(LikedArticle article);

    @Delete
    void removeLike(LikedArticle article);
}
