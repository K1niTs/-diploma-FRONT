// app/src/main/java/com/example/musicrental/ui/chat/ChatAdapter.java
package com.example.musicrental.ui.chat;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicrental.R;
import com.example.musicrental.data.MessageDto;
import com.example.musicrental.util.Prefs;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.VH> {
    private final List<MessageDto> data;
    private final long me = Prefs.get().getUserId();

    public ChatAdapter(List<MessageDto> data) { this.data = data; }

    @Override public int getItemViewType(int pos) {
        return data.get(pos).fromId.equals(me)
                ? R.layout.item_outgoing
                : R.layout.item_incoming;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int viewType) {
        View v = LayoutInflater.from(p.getContext())
                .inflate(viewType, p, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        h.tvText.setText(data.get(pos).text);
    }

    @Override public int getItemCount() { return data.size(); }

    public void setData(List<MessageDto> msgs) {
        data.clear(); data.addAll(msgs); notifyDataSetChanged();
    }
    public void add(MessageDto msg) {
        data.add(msg); notifyItemInserted(data.size()-1);
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvText;
        VH(View v) { super(v); tvText = v.findViewById(R.id.tvText); }
    }
}
