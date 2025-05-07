package com.example.musicrental.repository;

import com.example.musicrental.data.ChatRoomDto;
import com.example.musicrental.data.MessageDto;
import com.example.musicrental.network.ApiClient;
import com.example.musicrental.network.ChatApi;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatRepository {
    private final ChatApi api = ApiClient.get().create(ChatApi.class);

    public void history(long otherId, Callback<List<MessageDto>> cb) {
        api.history(otherId).enqueue(cb);
    }
    public void send(MessageDto msg, Callback<MessageDto> cb) {
        api.send(msg).enqueue(cb);
    }
    public void rooms(Callback<List<ChatRoomDto>> cb) {
        api.rooms().enqueue(cb);
    }
}