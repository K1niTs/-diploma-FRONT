package com.example.musicrental.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicrental.data.ChatRoomDto;
import com.example.musicrental.databinding.FragmentChatListBinding;
import com.example.musicrental.repository.ChatRepository;
import com.example.musicrental.ui.chat.ChatFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListFragment extends Fragment {

    private FragmentChatListBinding vb;
    private final ChatRepository repo = new ChatRepository();
    private final List<ChatRoomDto> data = new ArrayList<>();
    private ChatRoomAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vb = FragmentChatListBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view,
                                        @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ChatRoomAdapter(data, room -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            ((ViewGroup) requireView().getParent()).getId(),
                            ChatFragment.newInstance(room.otherId)
                    )
                    .addToBackStack(null)
                    .commit();
        });
        vb.rvChats.setLayoutManager(new LinearLayoutManager(requireContext()));
        vb.rvChats.setAdapter(adapter);

        vb.swipeChats.setOnRefreshListener(this::loadRooms);

        vb.swipeChats.setRefreshing(true);
        loadRooms();
    }

    private void loadRooms() {
        repo.rooms(new Callback<List<ChatRoomDto>>() {
            @Override public void onResponse(Call<List<ChatRoomDto>> call,
                                             Response<List<ChatRoomDto>> resp) {
                vb.swipeChats.setRefreshing(false);
                if (resp.isSuccessful() && resp.body() != null) {
                    adapter.setData(resp.body());
                } else {
                    Toast.makeText(requireContext(),
                            "Ошибка: " + resp.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<List<ChatRoomDto>> call,
                                            Throwable t) {
                vb.swipeChats.setRefreshing(false);
                Toast.makeText(requireContext(),
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        vb = null;
    }
}
