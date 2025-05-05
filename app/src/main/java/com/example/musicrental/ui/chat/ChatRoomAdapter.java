// app/src/main/java/com/example/musicrental/ui/chat/ChatRoomAdapter.java
package com.example.musicrental.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicrental.R;
import com.example.musicrental.data.ChatRoomDto;

import java.util.List;

public class ChatRoomAdapter
        extends RecyclerView.Adapter<ChatRoomAdapter.VH> {

    public interface Listener {
        void onClick(@NonNull ChatRoomDto room);
    }

    private final List<ChatRoomDto> data;
    private final Listener listener;

    public ChatRoomAdapter(@NonNull List<ChatRoomDto> data,
                           @NonNull Listener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_room, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ChatRoomDto room = data.get(position);
        holder.tvName.setText(room.otherEmail);
        holder.tvLastMessage.setText(room.lastText);
        holder.itemView.setOnClickListener(v -> listener.onClick(room));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(@NonNull List<ChatRoomDto> rooms) {
        data.clear();
        data.addAll(rooms);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvName, tvLastMessage;

        VH(@NonNull View itemView) {
            super(itemView);
            tvName        = itemView.findViewById(R.id.tvName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
        }
    }
}
