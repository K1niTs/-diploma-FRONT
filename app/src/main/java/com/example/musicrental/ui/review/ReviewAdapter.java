package com.example.musicrental.ui.review;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicrental.R;
import com.example.musicrental.data.ReviewDto;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.VH>{

    private final List<ReviewDto> data;
    private final DateTimeFormatter fmt =
            DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    .withLocale(Locale.getDefault())
                    .withZone(ZoneId.systemDefault());

    public ReviewAdapter(List<ReviewDto> d){ data = d; }

    static class VH extends RecyclerView.ViewHolder{
        TextView tvRating,tvComment,tvDate;
        VH(View v){
            super(v);
            tvRating  = v.findViewById(R.id.tvRating);
            tvComment = v.findViewById(R.id.tvComment);
            tvDate    = v.findViewById(R.id.tvDate);
        }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p,int t){
        return new VH(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_review,p,false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        ReviewDto r = data.get(i);

        h.tvRating .setText("★ " + r.rating);
        h.tvComment.setText(
                (r.comment == null || r.comment.isEmpty()) ? "—" : r.comment
        );

        OffsetDateTime odt = OffsetDateTime.parse(r.createdAt);
        h.tvDate.setText(fmt.format(odt));
    }
    @Override public int getItemCount(){ return data.size(); }
}
