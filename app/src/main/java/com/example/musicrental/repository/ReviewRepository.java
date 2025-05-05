package com.example.musicrental.repository;

import com.example.musicrental.data.ReviewDto;
import com.example.musicrental.network.ApiClient;
import com.example.musicrental.network.ReviewApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ReviewRepository {

    private final ReviewApi api = ApiClient.get().create(ReviewApi.class);

    /* ---- GET ---- */
    public void list(long instrumentId, Callback<List<ReviewDto>> cb){
        api.list(instrumentId).enqueue(cb);
    }

    /* ---- POST ---- */
    public void add(long instrumentId, int rating, String comment,
                    Callback<ReviewDto> cb){
        ReviewDto body = new ReviewDto(null, null, rating, comment, null);
        api.add(instrumentId, body).enqueue(cb);
    }
}
