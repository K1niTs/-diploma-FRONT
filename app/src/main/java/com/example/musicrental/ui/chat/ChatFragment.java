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
import com.example.musicrental.R;
import com.example.musicrental.data.MessageDto;
import com.example.musicrental.databinding.FragmentChatBinding;
import com.example.musicrental.repository.ChatRepository;
import com.example.musicrental.util.Prefs;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private static final String ARG_OTHER_ID = "otherId";

    public static ChatFragment newInstance(long otherId) {
        ChatFragment f = new ChatFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_OTHER_ID, otherId);
        f.setArguments(args);
        return f;
    }

    private FragmentChatBinding vb;
    private final ChatRepository repo = new ChatRepository();
    private final List<MessageDto> data = new ArrayList<>();
    private ChatAdapter adapter;
    private long otherId;

    @Nullable @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        vb = FragmentChatBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        otherId = requireArguments().getLong(ARG_OTHER_ID);

        adapter = new ChatAdapter(data);
        vb.rvChat.setLayoutManager(new LinearLayoutManager(requireContext()));
        vb.rvChat.setAdapter(adapter);

        vb.swipeChat.setOnRefreshListener(this::loadHistory);

        vb.messageInputLayout.setEndIconOnClickListener(v -> {
            String txt = vb.etMessage.getText().toString().trim();
            if (txt.isEmpty()) return;

            long me = Prefs.get().getUserId();
            MessageDto msg = new MessageDto(
                    null,
                    me,
                    otherId,
                    txt,
                    Instant.now().toString(),
                    null
            );

            repo.send(msg, new Callback<MessageDto>() {
                @Override public void onResponse(
                        Call<MessageDto> call,
                        Response<MessageDto> resp) {
                    if (resp.isSuccessful() && resp.body() != null) {
                        adapter.add(resp.body());
                        vb.etMessage.setText("");
                        vb.rvChat.scrollToPosition(data.size() - 1);
                    } else {
                        Toast.makeText(requireContext(),
                                "Ошибка: " + resp.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<MessageDto> call, Throwable t) {
                    Toast.makeText(requireContext(),
                            t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        loadHistory();
    }

    private void loadHistory() {
        vb.swipeChat.setRefreshing(true);
        repo.history(otherId, new Callback<List<MessageDto>>() {
            @Override public void onResponse(
                    Call<List<MessageDto>> call,
                    Response<List<MessageDto>> resp) {
                vb.swipeChat.setRefreshing(false);
                if (resp.isSuccessful() && resp.body() != null) {
                    adapter.setData(resp.body());
                    vb.rvChat.scrollToPosition(data.size() - 1);
                } else {
                    Toast.makeText(requireContext(),
                            "Не удалось загрузить историю",
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<List<MessageDto>> call, Throwable t) {
                vb.swipeChat.setRefreshing(false);
                Toast.makeText(requireContext(),
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
