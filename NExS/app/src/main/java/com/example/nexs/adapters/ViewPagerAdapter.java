package com.example.nexs.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nexs.MainActivity;
import com.example.nexs.R;
import com.example.nexs.models.Article;
import com.example.nexs.models.ArticleResponse;
import com.example.nexs.models.UserResponse;
import com.example.nexs.room.BookmarkedArticle;
import com.example.nexs.room.LikedArticle;
import com.example.nexs.room.viewmodel.LocalDataViewModel;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder> {

    private List<BookmarkedArticle> slideList;
    private Context context;
    private Set<String> bookmarkedIds = new HashSet<>();
    private Set<LikedArticle> likedIds = new HashSet<>();
    private LocalDataViewModel viewModel;
    public Long lastTime = 0L;
    boolean fetching = false;

    public void setBookmarkedIds(Set<String> bookmarkedIds) {
        this.bookmarkedIds = bookmarkedIds;
    }

    public void setLikedIds(Set<LikedArticle> likedIds) {
        this.likedIds = likedIds;
    }

    public ViewPagerAdapter(List<BookmarkedArticle> slideList, Context context) {
        this.slideList = slideList;
        this.context = context;
        viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(LocalDataViewModel.class);
    }

    @NonNull
    @Override
    public PagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PagerViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_slide, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PagerViewHolder holder, int position) {
        if (slideList.size() - position <= 7 && !fetching) {
            fetchMoreArticles();
        }
        holder.setPagerViewHolder(slideList.get(position));
    }

    private void fetchMoreArticles() {
        fetching = true;
        Call<ArticleResponse> call = MainActivity.api.articleGetAll(lastTime);
        call.enqueue(new Callback<ArticleResponse>() {
            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getCode() == 200) {
                        List<Article> articles = response.body().getArticles();
                        if (articles.size() == 0)
                            return;
                        lastTime = articles.get(articles.size() - 1).getCreatedAt();
                        for (Article a : articles) {
                            BookmarkedArticle slide = new BookmarkedArticle();
                            slide.setDescription(a.getDescription());
                            slide.setTitle(a.getTitle());
                            slide.setId(a.getId());
                            slide.setImgUrl(a.getImgUrl());
                            slide.setSourceUrl(a.getSourceUrl());
                            slide.setLikes(a.getLikes());
                            slide.setCategory(a.getCategory());
                            slide.setCreatedAt(a.getCreatedAt());
                            if (!slideList.contains(slide)) {
                                slideList.add(slide);
                                notifyItemInserted(slideList.size() - 1);
                            }
                        }
                    }
                }
                fetching = false;
            }

            @Override
            public void onFailure(Call<ArticleResponse> call, Throwable t) {
                fetching = false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return slideList.size();
    }

    class PagerViewHolder extends RecyclerView.ViewHolder {
        /*private TextView author;
        private TextView date;*/
        private TextView heading;
        private TextView desc;
        private ImageView image;
        private TextView tapToView;
        private TextView likesCount;
        private TextView readMore;
        private MaterialFavoriteButton likeButton, bookmarkButton;
        boolean skipLike, skipBookmark;

        public PagerViewHolder(@NonNull View itemView) {
            super(itemView);
            setReferences(itemView);
            setListeners();
        }

        private void setReferences(View itemView) {
            /*author = itemView.findViewById(R.id.slide_item_username);
            date = itemView.findViewById(R.id.slide_item_timestamp);*/
            heading = itemView.findViewById(R.id.slide_item_heading);
            desc = itemView.findViewById(R.id.slide_item_description);
            image = itemView.findViewById(R.id.slide_item_image);
            likesCount = itemView.findViewById(R.id.slide_item_like_count);
            tapToView = itemView.findViewById(R.id.slide_item_article_link);
            readMore = itemView.findViewById(R.id.textView_link);
            likeButton = itemView.findViewById(R.id.like_button);
            bookmarkButton = itemView.findViewById(R.id.save_button);
        }

        private void setListeners() {
            tapToView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent intent = builder.build();
                    intent.launchUrl(context, Uri.parse(slideList.get(getAdapterPosition()).getSourceUrl()));
                }
            });
            readMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    desc.setMaxLines(1000);
                }
            });
            setLikeListener();
            setBookmarkListener();
        }

        private void setLikeListener() {
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                        Toast.makeText(context, "Please sign in", Toast.LENGTH_LONG).show();
                        return;
                    }
                    likeButton.toggleFavorite(true);
                    LikedArticle likedArticle = new LikedArticle(slideList.get(getAdapterPosition()).getId());
                    if (!likedIds.contains(new LikedArticle(slideList.get(getAdapterPosition()).getId()))) {
                        viewModel.addLike(likedArticle);
                        Call<ArticleResponse> call = MainActivity.api.articleLikeById(
                                MainActivity.token,
                                slideList.get(getAdapterPosition()).getId()
                        );
                        call.enqueue(new Callback<ArticleResponse>() {
                            @Override
                            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                                boolean fav;
                                if (response.code() != 200) {
                                    Log.i("news", response.body().getMessage());
                                    fav = false;
                                } else {
                                    if (Objects.requireNonNull(response.body()).getCode() == 200) {
                                        fav = true;
                                    } else {
                                        Log.i("news", response.body().getMessage());
                                        fav = false;
                                    }
                                }
                                if (!fav) {
                                    likeButton.toggleFavorite(true);
                                    viewModel.removeLike(likedArticle);
                                }
                            }

                            @Override
                            public void onFailure(Call<ArticleResponse> call, Throwable t) {
                                Log.i("news", t.getMessage());
                                likeButton.toggleFavorite(true);
                                viewModel.removeLike(likedArticle);
                            }
                        });
                    } else {
                        viewModel.removeLike(likedArticle);
                        Call<ArticleResponse> call = MainActivity.api.articleDislikeById(
                                MainActivity.token,
                                slideList.get(getAdapterPosition()).getId()
                        );
                        call.enqueue(new Callback<ArticleResponse>() {
                            @Override
                            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                                boolean fav;
                                if (response.code() != 200) {
                                    Log.i("news", response.body().getMessage());
                                    fav = true;
                                } else {
                                    if (Objects.requireNonNull(response.body()).getCode() == 200) {
                                        fav = false;
                                    } else {
                                        Log.i("news", response.body().getMessage());
                                        fav = true;
                                    }
                                }
                                if (fav) {
                                    likeButton.toggleFavorite(true);
                                    viewModel.addLike(likedArticle);
                                }
                            }

                            @Override
                            public void onFailure(Call<ArticleResponse> call, Throwable t) {
                                Log.i("news", t.getMessage());
                                likeButton.toggleFavorite(true);
                                viewModel.addLike(likedArticle);
                            }
                        });
                    }
                }
            });
        }

        private void setBookmarkListener() {
            bookmarkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                        Toast.makeText(context, "Please sign in", Toast.LENGTH_LONG).show();
                        return;
                    }
                    bookmarkButton.toggleFavorite(true);
                    if (!bookmarkedIds.contains(slideList.get(getAdapterPosition()).getId())) {
                        viewModel.addBookmark(slideList.get(getAdapterPosition()));
                        Call<UserResponse> call = MainActivity.api.userAddBookmark(
                                MainActivity.token,
                                slideList.get(getAdapterPosition()).getId()
                        );
                        call.enqueue(new Callback<UserResponse>() {
                            @Override
                            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                                boolean fav;
                                if (response.code() != 200) {
                                    Log.i("news", response.body().getMessage());
                                    fav = true;
                                } else {
                                    if (response.body().getCode() == 200) {
                                        fav = true;
                                    } else {
                                        Log.i("news", response.body().getMessage());
                                        fav = false;
                                    }
                                }
                                if (!fav) {
                                    bookmarkButton.toggleFavorite(true);
                                    viewModel.removeBookmark(slideList.get(getAdapterPosition()));
                                }
                            }

                            @Override
                            public void onFailure(Call<UserResponse> call, Throwable t) {
                                Log.i("news", t.getMessage());
                                bookmarkButton.toggleFavorite(true);
                                viewModel.removeBookmark(slideList.get(getAdapterPosition()));
                            }
                        });
                    } else {
                        viewModel.removeBookmark(slideList.get(getAdapterPosition()));
                        Call<UserResponse> call = MainActivity.api.userRemoveBookmark(
                                MainActivity.token,
                                slideList.get(getAdapterPosition()).getId()
                        );
                        call.enqueue(new Callback<UserResponse>() {
                            @Override
                            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                                boolean fav;
                                if (response.code() != 200) {
                                    Log.i("news", response.body().getMessage());
                                    fav = true;
                                } else {
                                    if (response.body().getCode() == 200) {
                                        fav = false;
                                    } else {
                                        Log.i("news", response.body().getMessage());
                                        fav = true;
                                    }
                                }
                                if (fav) {
                                    bookmarkButton.toggleFavorite(true);
                                    viewModel.addBookmark(slideList.get(getAdapterPosition()));
                                }
                            }

                            @Override
                            public void onFailure(Call<UserResponse> call, Throwable t) {
                                Log.i("news", t.getMessage());
                                bookmarkButton.toggleFavorite(true);
                                viewModel.addBookmark(slideList.get(getAdapterPosition()));
                            }
                        });
                    }
                }
            });
        }

//        void changeMaterialFavButtonStatus(MaterialFavoriteButton button, boolean set) {
//            button.post(new Runnable() {
//                @Override
//                public void run() {
//                    button.setFavorite(set);
//                }
//            });
//        }


        void setPagerViewHolder(BookmarkedArticle slide) {
            /*author.setText(slide.getAuthor());
            date.setText(slide.getDate());*/
            heading.setText(slide.getTitle());
            desc.setMaxLines(11);
            desc.setText(slide.getDescription());
            likesCount.setText(String.valueOf(slide.getLikes()));
            Glide.with(context)
                    .setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.placeholder))
                    .load(slide.getImgUrl())
                    .into(image);
            //Setting like and bookmark
            String id = slide.getId();
            LikedArticle article = new LikedArticle(id);
            if (bookmarkedIds.contains(id)) {
                if (!bookmarkButton.isFavorite()) {
                    skipBookmark = true;
                    bookmarkButton.setFavorite(true);
                }
            } else {
                if (bookmarkButton.isFavorite()) {
                    skipBookmark = true;
                    bookmarkButton.setFavorite(false);
                }
            }
            if (likedIds.contains(article)) {
                if (!likeButton.isFavorite()) {
                    skipLike = true;
                    likeButton.setFavorite(true);
                }
            } else {
                if (likeButton.isFavorite()) {
                    skipLike = true;
                    likeButton.setFavorite(false);
                }
            }
        }

    }


}

