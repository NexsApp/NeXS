package in.co.nexs.nexsapp.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import in.co.nexs.nexsapp.room.BookmarkedArticle;
import in.co.nexs.nexsapp.room.LikedArticle;

import java.util.List;

public class LocalDataViewModel extends AndroidViewModel {
    private Repository repository;
    private final LiveData<List<BookmarkedArticle>> bookmarkedArticles;
    private final LiveData<List<String>> bookmarkedIds;
    private final LiveData<List<LikedArticle>> likedArticles;

    public LocalDataViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        bookmarkedArticles = repository.getBookmarks();
        bookmarkedIds = repository.getBookmarkedIds();
        likedArticles = repository.getLikes();
    }

    public LiveData<List<BookmarkedArticle>> getBookmarks() {
        return bookmarkedArticles;
    }

    public LiveData<List<String>> getBookmarkedIds() {
        return bookmarkedIds;
    }

    public void addBookmark(BookmarkedArticle article) {
        repository.addBookmark(article);
    }

    public void updateBookmark(BookmarkedArticle article) {
        repository.updateBookmark(article);
    }

    public void removeBookmark(BookmarkedArticle article) {
        repository.removeBookmark(article);
    }

    public void deleteAllBookmarks() {
        repository.deleteAllBookmarks();
    }

    public LiveData<List<LikedArticle>> getLikes() {
        return likedArticles;
    }

    public void addLike(LikedArticle article) {
        repository.addLike(article);
    }

    public void removeLike(LikedArticle article) {
        repository.removeLike(article);
    }

    public void deleteAllLikes() {
        repository.deleteAllLikes();
    }
}
