// app/src/main/java/com/example/musicrental/repository/BookingRepository.java
package com.example.musicrental.repository;

import com.example.musicrental.data.BookingDto;
import com.example.musicrental.network.ApiClient;
import com.example.musicrental.network.BookingApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class BookingRepository {

    private final BookingApi api =
            ApiClient.get().create(BookingApi.class);

    public void create(BookingDto dto, Callback<BookingDto> cb){
        api.create(dto).enqueue(cb);
    }
    public void my(Callback<List<BookingDto>> cb){ api.my().enqueue(cb); }

    public void cancel(long id, Callback<BookingDto> cb){ api.cancel(id).enqueue(cb); }
    public void pay(long id,      Callback<BookingDto>        cb){ api.pay(id).enqueue(cb); }
}
