// app/src/main/java/com/example/musicrental/network/BookingApi.java
package com.example.musicrental.network;

import com.example.musicrental.data.BookingDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface BookingApi {

    @POST("bookings")
    Call<BookingDto> create(@Body BookingDto dto);

    @GET("bookings")
    Call<List<BookingDto>> list(@Query("userId") Long uid);       // на будущее
    @GET("bookings/my")
    Call<List<BookingDto>> my();

    @PATCH("bookings/{id}/cancel")
    Call<BookingDto> cancel(@Path("id") long id);
    /* --- оплата ---------------------------------------------------- */
    @POST("bookings/{id}/pay")
    Call<BookingDto> pay(@Path("id") long id);    // backend вернёт BookingDto c paymentUrl
}
