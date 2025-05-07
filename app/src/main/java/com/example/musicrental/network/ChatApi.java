package com.example.musicrental.network;

import com.example.musicrental.data.ChatRoomDto;
import com.example.musicrental.data.MessageDto;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ChatApi {
    @GET("chat/{otherId}")
    Call<List<MessageDto>> history(@Path("otherId") long otherId);

    @POST("chat/send")
    Call<MessageDto> send(@Body MessageDto msg);

    @GET("chat/rooms")
    Call<List<ChatRoomDto>> rooms();
}