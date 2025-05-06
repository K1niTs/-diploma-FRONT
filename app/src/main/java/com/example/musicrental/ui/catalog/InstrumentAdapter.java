// app/src/main/java/com/example/musicrental/ui/catalog/InstrumentAdapter.java
package com.example.musicrental.ui.catalog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import coil.request.ImageRequest;
import com.example.musicrental.R;
import com.example.musicrental.data.InstrumentDto;

import java.util.List;
import java.util.Locale;

public class InstrumentAdapter
        extends RecyclerView.Adapter<InstrumentAdapter.VH> {

    public interface OnItemClick { void click(InstrumentDto dto); }

    private final List<InstrumentDto> items;
    private final OnItemClick         click;

    public InstrumentAdapter(List<InstrumentDto> items, OnItemClick click) {
        this.items = items;
        this.click = click;
    }

    static final class VH extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvTitle, tvCategory, tvDesc, tvPrice;

        VH(@NonNull View v) {
            super(v);
            ivPhoto    = v.findViewById(R.id.ivPhoto);
            tvTitle    = v.findViewById(R.id.tvTitle);
            tvCategory = v.findViewById(R.id.tvCategory);
            tvDesc     = v.findViewById(R.id.tvDesc);
            tvPrice    = v.findViewById(R.id.tvPrice);
        }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_instrument, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        InstrumentDto d = items.get(pos);

        h.tvTitle.setText(d.title);
        h.tvCategory.setText(d.category);
        h.tvDesc.setText(d.description);
        h.tvPrice.setText(
                String.format(Locale.getDefault(),"%.0f ₽/день", d.pricePerDay)
        );

        ImageRequest req = new ImageRequest.Builder(h.ivPhoto.getContext())
                .data(d.imageUrl)
                .target(h.ivPhoto)
                .placeholder(R.drawable.for_add)
                .error(R.drawable.for_add)
                .build();
        coil.Coil.imageLoader(h.ivPhoto.getContext()).enqueue(req);

        h.itemView.setOnClickListener(v -> click.click(d));
    }

    @Override public int getItemCount() {
        return items.size();
    }
}
