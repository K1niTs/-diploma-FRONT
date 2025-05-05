// app/src/main/java/com/example/musicrental/ui/booking/BookDialog.java
package com.example.musicrental.ui.booking;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.*;
import androidx.fragment.app.DialogFragment;

import com.example.musicrental.data.*;
import com.example.musicrental.databinding.DialogBookBinding;
import com.example.musicrental.repository.BookingRepository;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.*;
import retrofit2.*;

public class BookDialog extends DialogFragment {

    private static final String ARG_INST = "inst";

    public static BookDialog newInstance(InstrumentDto dto){
        Bundle b = new Bundle();
        b.putSerializable(ARG_INST, dto);
        BookDialog d = new BookDialog(); d.setArguments(b);
        return d;
    }

    /* --- fields --- */

    private DialogBookBinding vb;
    private InstrumentDto     inst;
    private final BookingRepository repo = new BookingRepository();

    /* ---------- life-cycle ---------- */

    @NonNull @Override
    public Dialog onCreateDialog(@Nullable Bundle s) {
        vb   = DialogBookBinding.inflate(getLayoutInflater());
        inst = (InstrumentDto) requireArguments().getSerializable(ARG_INST);

        vb.tvInstrument.setText(inst.title);

        vb.etFrom.setOnClickListener(v -> pickDate(vb.etFrom));
        vb.etTo  .setOnClickListener(v -> pickDate(vb.etTo));
        vb.btnOk .setOnClickListener(v -> tryBook());

        Dialog d = new Dialog(requireContext(),
                com.google.android.material.R.style.ThemeOverlay_Material3_BottomSheetDialog);
        d.setContentView(vb.getRoot());
        return d;
    }

    /* ---------- helpers ---------- */

    private void pickDate(View target){
        MaterialDatePicker<Long> dp = MaterialDatePicker.Builder.datePicker().build();
        dp.addOnPositiveButtonClickListener(ts ->
                ((android.widget.EditText) target).setText(
                        LocalDate.ofInstant(Instant.ofEpochMilli(ts),
                                ZoneId.systemDefault()).toString()));
        dp.show(getParentFragmentManager(), "dp");
    }

    private void tryBook(){
        if (vb.etFrom.getText().length()==0 || vb.etTo.getText().length()==0){
            toast("Выберите даты"); return;
        }
        LocalDate from = LocalDate.parse(vb.etFrom.getText());
        LocalDate to   = LocalDate.parse(vb.etTo.getText());
        if (!to.isAfter(from)) { toast("Дата окончания должна быть позже"); return; }

        BookingDto dto = new BookingDto(
                null,                                           // id
                com.example.musicrental.util.Prefs.get().getUserId(),
                inst.id,
                inst.title,                                     // instrumentTitle
                from, to,
                0, "NEW",
                null                                            // paymentUrl – создаст backend
        );

        repo.create(dto, new Callback<>() {
            @Override public void onResponse(@NonNull Call<BookingDto> c,
                                             @NonNull Response<BookingDto> r) {
                if (r.isSuccessful() && r.body()!=null) {
                    BookingDto resp = r.body();
                    /* если сервер сразу вернул ссылку на оплату — откроем её */
                    if (resp.paymentUrl != null) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(resp.paymentUrl)));
                    }
                    toast("Бронь создана!");
                    dismiss();
                } else if (r.code()==409){
                    toast("Инструмент уже забронирован на эти даты");
                } else toast("Ошибка "+r.code());
            }
            @Override public void onFailure(@NonNull Call<BookingDto> c,
                                            @NonNull Throwable t){
                toast(t.getMessage());
            }
        });
    }

    private void toast(String t){
        Toast.makeText(requireContext(), t, Toast.LENGTH_SHORT).show();
    }
}
