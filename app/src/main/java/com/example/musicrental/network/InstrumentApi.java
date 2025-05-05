// app/src/main/java/com/example/musicrental/network/InstrumentApi.java
package com.example.musicrental.network;

import com.example.musicrental.data.InstrumentDto;
import com.example.musicrental.model.Page;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

/** Retrofit-контракт для энд-поинтов instruments */
public interface InstrumentApi {

    /** список с фильтрами */
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

    /** create */
    @POST("instruments")
    Call<InstrumentDto> add(@Body InstrumentDto dto);

    /** update существующего по ID */
    @PUT("instruments/{id}")
    Call<InstrumentDto> update(
            @Path("id") long id,
            @Body     InstrumentDto dto
    );

    /** delete по ID */
    @DELETE("instruments/{id}")
    Call<Void> delete(@Path("id") long id);

    /** upload photo */
    @Multipart
    @POST("instruments/{id}/photo")
    Call<InstrumentDto> uploadPhoto(
            @Path("id") long id,
            @Part       MultipartBody.Part file
    );
}
