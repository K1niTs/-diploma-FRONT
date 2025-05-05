// app/src/main/java/com/example/musicrental/ui/chat/ChatFragment.java
package com.example.musicrental.ui.chat;

import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.*;
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
import retrofit2.*;

public class ChatFragment extends Fragment {

    private static final String ARG_OTHER_ID = "otherId";

    public static ChatFragment newInstance(long otherId) {
        ChatFragment f = new ChatFragment();
        Bundle b = new Bundle();
        b.putLong(ARG_OTHER_ID, otherId);
        f.setArguments(b);
        return f;
    }

    private FragmentChatBinding vb;
    private final ChatRepository repo = new ChatRepository();
    private final List<MessageDto> data = new ArrayList<>();
    private ChatAdapter adapter;
    private long otherId;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vb = FragmentChatBinding.inflate(inf, container, false);
        return vb.getRoot();
    }

    @Override public void onViewCreated(
            @NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);
        otherId = requireArguments().getLong(ARG_OTHER_ID);

        adapter = new ChatAdapter(data);
        vb.rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        vb.rvChat.setAdapter(adapter);

        loadHistory();

        vb.btnSend.setOnClickListener(x -> {
            String txt = vb.etMessage.getText().toString().trim();
            if (txt.isEmpty()) return;
            long me = Prefs.get().getUserId();
            MessageDto msg = new MessageDto(
                    null, me, otherId, txt, Instant.now().toString()
            );
            repo.send(msg, new Callback<>() {
                @Override public void onResponse(Call<MessageDto> c, Response<MessageDto> r) {
                    if (r.isSuccessful() && r.body()!=null) {
                        adapter.add(r.body());
                        vb.etMessage.setText("");
                        vb.rvChat.scrollToPosition(data.size()-1);
                    }
                }
                @Override public void onFailure(Call<MessageDto> c, Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadHistory() {
        repo.history(otherId, new Callback<>() {
            @Override public void onResponse(Call<List<MessageDto>> c,
                                             Response<List<MessageDto>> r) {
                if (r.isSuccessful() && r.body()!=null) {
                    adapter.setData(r.body());
                    vb.rvChat.scrollToPosition(data.size()-1);
                }
            }
            @Override public void onFailure(Call<List<MessageDto>> c, Throwable t){ }
        });
    }
}
