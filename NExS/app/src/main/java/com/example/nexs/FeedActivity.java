package com.example.nexs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.nexs.adapters.ViewPagerAdapter;
import com.example.nexs.models.Article;
import com.example.nexs.models.ArticleResponse;
import com.example.nexs.room.BookmarkedArticle;
import com.example.nexs.room.LikedArticle;
import com.example.nexs.room.viewmodel.LocalDataViewModel;
import com.example.nexs.utility.LoadingDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedActivity extends AppCompatActivity {

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager2 viewPager2;
    private List<BookmarkedArticle> slideList = new ArrayList<>();
    private LocalDataViewModel viewModel;
    private boolean showBookmarks;
    private boolean showById;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        viewPagerAdapter = new ViewPagerAdapter(slideList, this);
        viewPager2 = findViewById(R.id.viewPagerSlider);
        viewPager2.setPageTransformer(new DepthPageTransformer());
        showBookmarks = shouldShowBookmarkOnly();
        showById = shouldShowById();
        setViewModel();
        if (showById) {
            fetchAndShow();
        } else if (!showBookmarks) {
            createSlides();
            viewPagerAdapter.lastTime = slideList.get(slideList.size() - 1).getCreatedAt();
        }
        //viewPager2.setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager2.setAdapter(viewPagerAdapter);
    }

    private void fetchAndShow() {
        LoadingDialog dialog = new LoadingDialog(this);
        dialog.showDialog();
        Call<ArticleResponse> call = MainActivity.api.articleGetById(getIntent().getStringExtra("articleId"));
        call.enqueue(new Callback<ArticleResponse>() {
            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                dialog.stopDialog();
                if (response.code() == 200) {
                    if (response.body().getCode() == 200) {
                        for (Article a : response.body().getArticles()) {
                            BookmarkedArticle slide = new BookmarkedArticle();
                            slide.setDescription(a.getDescription());
                            slide.setTitle(a.getTitle());
                            slide.setId(a.getId());
                            slide.setImgUrl(a.getImgUrl());
                            slide.setSourceUrl(a.getSourceUrl());
                            slide.setLikes(a.getLikes());
                            slide.setCategory(a.getCategory());
                            slide.setCreatedAt(a.getCreatedAt());
                            slideList.add(slide);
                            viewPagerAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(FeedActivity.this, "Oops! Something went Wrong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FeedActivity.this, "Oops! Something went Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArticleResponse> call, Throwable t) {
                dialog.stopDialog();
                Toast.makeText(FeedActivity.this, "Oop! Something went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean shouldShowById() {
        Intent intent = getIntent();
        return intent.getBooleanExtra("showById", false);
    }

    private boolean shouldShowBookmarkOnly() {
        Intent intent = getIntent();
        return intent.getBooleanExtra("showBookmarks", false);
    }

    private void setViewModel() {
        viewModel = new ViewModelProvider(this).get(LocalDataViewModel.class);
        viewModel.getBookmarkedIds().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                Set<String> set = new HashSet<>(strings);
                viewPagerAdapter.setBookmarkedIds(set);
                viewPagerAdapter.notifyItemChanged(viewPager2.getCurrentItem());
            }
        });
        viewModel.getLikes().observe(this, new Observer<List<LikedArticle>>() {
            @Override
            public void onChanged(List<LikedArticle> likedArticles) {
                Set<LikedArticle> set = new HashSet<>(likedArticles);
                viewPagerAdapter.setLikedIds(set);
                viewPagerAdapter.notifyItemChanged(viewPager2.getCurrentItem());
            }
        });
        if (showBookmarks) {
            viewModel.getBookmarks().observe(this, new Observer<List<BookmarkedArticle>>() {
                @Override
                public void onChanged(List<BookmarkedArticle> bookmarkedArticles) {
                    slideList.addAll(bookmarkedArticles);
                    viewModel.getBookmarks().removeObserver(this::onChanged);
                }
            });
        }
    }

    private void createSlides() {
        for (Article a : MainActivity.articles) {
            BookmarkedArticle slide = new BookmarkedArticle();
            slide.setDescription(a.getDescription());
            slide.setTitle(a.getTitle());
            slide.setId(a.getId());
            slide.setImgUrl(a.getImgUrl());
            slide.setSourceUrl(a.getSourceUrl());
            slide.setLikes(a.getLikes());
            slide.setCategory(a.getCategory());
            slide.setCreatedAt(a.getCreatedAt());
            slideList.add(slide);
        }
    }

    private String getDate(Long createdAt) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(createdAt);
        return simpleDateFormat.format(calendar.getTime());
    }

    @RequiresApi(21)
    public static class DepthPageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1f);
                view.setTranslationX(0f);
                view.setTranslationZ(0f);
                view.setScaleX(1f);
                view.setScaleY(1f);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationY(pageWidth * -position);
                // Move it behind the left page
                view.setTranslationZ(-1f);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }

}