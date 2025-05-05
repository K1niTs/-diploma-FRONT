package com.example.musicrental.ui.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicrental.R;
import com.example.musicrental.data.BookingDto;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.VH> {

    /* коллбэки */
    interface OnCancel { void cancel(BookingDto b); }
    interface OnAction { void action(BookingDto b); }   // Pay или Review
    interface OnOpen   { void open  (BookingDto b); }   // перейти в details

    private final List<BookingDto> data;
    private final OnCancel cbCancel;
    private final OnAction cbAction;
    private final OnOpen   cbOpen;

    BookingAdapter(List<BookingDto> d,
                   OnCancel cancel,
                   OnAction action,
                   OnOpen   open){
        data     = d;
        cbCancel = cancel;
        cbAction = action;
        cbOpen   = open;
    }

    /* holder */
    static class VH extends RecyclerView.ViewHolder{
        final TextView tvInstr,tvDates,tvStatus,tvCost;
        final MaterialButton btnAction;
        VH(View v){
            super(v);
            tvInstr   = v.findViewById(R.id.tvInstr);
            tvDates   = v.findViewById(R.id.tvDates);
            tvStatus  = v.findViewById(R.id.tvStatus);
            tvCost    = v.findViewById(R.id.tvCost);
            btnAction = v.findViewById(R.id.btnAction);
        }
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p,int t){
        return new VH(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_booking, p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h,int i){
        BookingDto b = data.get(i);

        h.tvInstr .setText(b.instrumentTitle);
        h.tvDates .setText(b.startDate + " → " + b.endDate);
        h.tvStatus.setText(b.status);
        h.tvCost  .setText(String.format(Locale.getDefault(),"%.0f ₽", b.totalCost));

        /* ---------- кнопка ---------- */
        boolean needPay    = "NEW".equals(b.status);
        boolean needReview = "WAITING_PAYMENT".equals(b.status);

        if (needPay){
            h.btnAction.setText(R.string.pay);     // строка «Оплатить»
        } else if (needReview){
            h.btnAction.setText(R.string.review);  // строка «Отзыв»
        }
        h.btnAction.setVisibility( (needPay||needReview)? View.VISIBLE:View.GONE );
        h.btnAction.setOnClickListener(v -> cbAction.action(b));

        /* ---------- клик по карточке ---------- */
        h.itemView.setOnClickListener(v -> cbOpen.open(b));
    }

    @Override public int getItemCount(){ return data.size(); }
}
