package com.example.musicrental.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public ChatAdapter(List<MessageDto> data) {
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        MessageDto msg = data.get(position);
        return msg.fromId.equals(me)
                ? R.layout.item_outgoing
                : R.layout.item_incoming;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        MessageDto msg = data.get(position);

        if (msg.fromId.equals(me)) {
            holder.tvSender.setText("Я");
        } else {
            holder.tvSender.setText(msg.fromEmail != null
                    ? msg.fromEmail
                    : "");
        }

        holder.tvText.setText(msg.text);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<MessageDto> msgs) {
        data.clear();
        data.addAll(msgs);
        notifyDataSetChanged();
    }

    public void add(MessageDto msg) {
        data.add(msg);
        notifyItemInserted(data.size() - 1);
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvSender, tvText;
        VH(View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tvSender);
            tvText   = itemView.findViewById(R.id.tvText);
        }
    }
}
