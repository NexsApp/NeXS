package in.co.nexs.nexsapp.room.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;

import in.co.nexs.nexsapp.room.AppDatabase;
import in.co.nexs.nexsapp.room.BookmarkDao;
import in.co.nexs.nexsapp.room.BookmarkedArticle;
import in.co.nexs.nexsapp.room.LikeDao;
import in.co.nexs.nexsapp.room.LikedArticle;

import java.util.List;

public class Repository {
    private final LiveData<List<BookmarkedArticle>> bookmarkedArticles;
    private final LiveData<List<String>> bookmarkedIds;
    private final LiveData<List<LikedArticle>> likedArticles;
    private final BookmarkDao bookmarkDao;
    private final LikeDao likeDao;

    public Repository(Application application) {
        AppDatabase db = AppDatabase.getAppDatabase(application);
        bookmarkDao = db.getBookmarkDao();
        bookmarkedArticles = bookmarkDao.getBookmarks();
        bookmarkedIds = bookmarkDao.getBookmarkIds();
        likeDao = db.getLikeDao();
        likedArticles = likeDao.getLikes();
    }

    public LiveData<List<BookmarkedArticle>> getBookmarks() {
        return bookmarkedArticles;
    }

    public LiveData<List<String>> getBookmarkedIds() {
        return bookmarkedIds;
    }

    public void addBookmark(BookmarkedArticle article) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                bookmarkDao.addBookmark(article);
            }
        };
        performBackgroundTask(runnable);
    }

    public void updateBookmark(BookmarkedArticle article) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                bookmarkDao.updateBookmark(article.getLikes(), article.getId());
            }
        };
        performBackgroundTask(runnable);
    }

    public void removeBookmark(BookmarkedArticle article) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                bookmarkDao.removeBookmark(article);
            }
        };
        performBackgroundTask(runnable);
    }

    public void deleteAllBookmarks() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                bookmarkDao.deleteAll();
            }
        };
        performBackgroundTask(runnable);
    }

    public LiveData<List<LikedArticle>> getLikes() {
        return likedArticles;
    }

    public void addLike(LikedArticle article) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                likeDao.addLike(article);
            }
        };
        performBackgroundTask(runnable);
    }

    public void removeLike(LikedArticle article) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                likeDao.removeLike(article);
            }
        };
        performBackgroundTask(runnable);
    }

    public void deleteAllLikes() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                likeDao.deleteAll();
            }
        };
        performBackgroundTask(runnable);
    }

    private void performBackgroundTask(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
