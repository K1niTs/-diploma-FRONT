package com.example.musicrental.network;

import com.example.musicrental.data.ReviewDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReviewApi {

    @GET("instruments/{id}/reviews")
    Call<List<ReviewDto>> list(@Path("id") long instrumentId);

    @POST("instruments/{id}/reviews")
    Call<ReviewDto> add(@Path("id") long instrumentId,
                        @Body ReviewDto body);
}
