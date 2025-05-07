package com.example.musicrental.network;

import com.example.musicrental.data.InstrumentDto;
import com.example.musicrental.model.Page;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface InstrumentApi {

    @GET("instruments")
    Call<Page<InstrumentDto>> list(
            @Query("q")        String q,
            @Query("category") String category,
            @Query("minPrice") Double minPrice,
            @Query("maxPrice") Double maxPrice,
            @Query("page")     int page,
            @Query("size")     int size,
            @Query("sort")     String sort
    );

    @POST("instruments")
    Call<InstrumentDto> add(@Body InstrumentDto dto);

    @PUT("instruments/{id}")
    Call<InstrumentDto> update(
            @Path("id") long id,
            @Body     InstrumentDto dto
    );

    @DELETE("instruments/{id}")
    Call<Void> delete(@Path("id") long id);

    @Multipart
    @POST("instruments/{id}/photo")
    Call<InstrumentDto> uploadPhoto(
            @Path("id") long id,
            @Part       MultipartBody.Part file
    );
}
