// app/src/main/java/com/example/musicrental/ui/chat/ChatListFragment.java
package com.example.musicrental.ui.chat;

import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.musicrental.R;
import com.example.musicrental.data.ChatRoomDto;
import com.example.musicrental.databinding.FragmentChatListBinding;
import com.example.musicrental.repository.ChatRepository;
import java.util.*;
import retrofit2.*;

public class ChatListFragment extends Fragment {
    private FragmentChatListBinding vb;
    private final ChatRepository repo = new ChatRepository();
    private final List<ChatRoomDto> data = new ArrayList<>();
    private ChatRoomAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup container,
                             @Nullable Bundle saved) {
        vb = FragmentChatListBinding.inflate(inf, container, false);
        return vb.getRoot();
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);

        adapter = new ChatRoomAdapter(data, room -> {
            // открываем конкретный чат
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            ((ViewGroup) requireView().getParent()).getId(),
                            ChatFragment.newInstance(room.otherId)
                    )
                    .addToBackStack(null)
                    .commit();
        });

        vb.rvChats.setLayoutManager(new LinearLayoutManager(getContext()));
        vb.rvChats.setAdapter(adapter);

        vb.swipeChats.setOnRefreshListener(this::loadRooms);
        vb.swipeChats.setRefreshing(true);
        loadRooms();
    }

    private void loadRooms() {
        repo.rooms(new Callback<List<ChatRoomDto>>() {
            @Override public void onResponse(Call<List<ChatRoomDto>> c,
                                             Response<List<ChatRoomDto>> r) {
                vb.swipeChats.setRefreshing(false);
                if (r.isSuccessful() && r.body()!=null) {
                    adapter.setData(r.body());
                } else {
                    Toast.makeText(getContext(),
                            "Ошибка: "+r.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<List<ChatRoomDto>> c,
                                            Throwable t) {
                vb.swipeChats.setRefreshing(false);
                Toast.makeText(getContext(),
                        t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        vb = null;
    }
}
