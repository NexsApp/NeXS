package com.example.nexs.api;

import androidx.annotation.NonNull;

import com.example.nexs.models.Article;
import com.example.nexs.models.ArticleResponse;
import com.example.nexs.models.User;
import com.example.nexs.models.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface NexsApi {

    //ARTICLE FUNCTIONS
    public static final String GET_ALL = "getAll";
    public static final String GET_BY_ID = "getOne";
    public static final String GET_BY_CATEGORY = "getByCategory";
    public static final String SEARCH = "search";
    public static final String DELETE_BY_ID = "deleteOne";
    public static final String UPDATE_BY_ID = "updateOne";
    public static final String LIKE_BY_ID = "like";
    public static final String DISLIKE_BY_ID = "dislike";
    public static final String NEW_POST = "newPost";

    //USER FUNCTIONS
    public static final String NEW_USER = "newUser";
    public static final String GET_USER_BY_ID = "getUser";
    public static final String ADD_BOOKMARK = "addBookmark";
    public static final String REMOVE_BOOKMARK = "removeBookmark";
    public static final String ADD_COINS = "addCoin";

    @GET("/?resource=article&func=" + GET_ALL)
    Call<ArticleResponse> articleGetAll(@Query("lastArticleTime") Long lastArticleTime);

    @GET("/?resource=article&func=" + GET_BY_ID)
    Call<ArticleResponse> articleGetById(@Query("id") @NonNull String id);

    @GET("/?resource=article&func=" + GET_BY_CATEGORY)
    Call<ArticleResponse> articleGetByCategory(@Query("category") @NonNull String category);

    @GET("/?resource=article&func=" + SEARCH)
    Call<ArticleResponse> searchArticles(@Query("category") @NonNull String text);

    @DELETE("/?resource=article&func=" + DELETE_BY_ID)
        //ADMIN
    Call<ArticleResponse> articleDeleteById(@Query("id") @NonNull String id);

    @PUT("/?resource=article&func=" + UPDATE_BY_ID)
        //ADMIN
    Call<ArticleResponse> articleUpdateById(@Query("id") @NonNull String id, @Body @NonNull Article article);

    @POST("/?resource=article&func=" + NEW_POST)
        //ADMIN
    Call<ArticleResponse> articleNewPost(@Body @NonNull Article article);

    @PUT("/?resource=article&func=" + LIKE_BY_ID)
    Call<ArticleResponse> articleLikeById(@Header("Authorization") @NonNull String token, @Query("id") @NonNull String id);

    @DELETE("/?resource=article&func=" + DISLIKE_BY_ID)
    Call<ArticleResponse> articleDislikeById(@Header("Authorization") @NonNull String token, @Query("id") @NonNull String id);

    @POST("/?resource=user&func=" + NEW_USER)
    Call<UserResponse> userNewUser(@Body @NonNull User user);

    @GET("/?resource=user&func=" + GET_USER_BY_ID)
    Call<UserResponse> userGetUserById(@Query("uid") String uid);

    @PUT("/?resource=user&func=" + ADD_BOOKMARK)
    Call<UserResponse> userAddBookmark(@Header("Authorization") @NonNull String token, @Query("articleid") String articleId);

    @DELETE("/?resource=user&func=" + REMOVE_BOOKMARK)
    Call<UserResponse> userRemoveBookmark(@Header("Authorization") @NonNull String token, @Query("articleid") String articleId);

    @PUT("/?resource=user&func=" + ADD_COINS)
    Call<UserResponse> userAddCoin(@Header("Authorization") @NonNull String token, @Query("amount") int amt);
}
