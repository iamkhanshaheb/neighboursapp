package com.neighbours.neighbours.network;


import com.neighbours.neighbours.models.FeedResponse;
import com.neighbours.neighbours.models.PostResponse;
import com.neighbours.neighbours.models.SuccessResponse;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

/**
 * Created by Joker on 9/22/16.
 */

public interface GoromchaApi {
    @GET("get_posts")
    Call<ArrayList<FeedResponse>> getPosts();

    @GET("get_post/{id}")
    Call<PostResponse> getPost(@Path("id") int id);

    @Multipart
    @POST("add_post")
    Call<SuccessResponse> addPost(@PartMap Map<String, String> params, @Part MultipartBody.Part photo);
}
