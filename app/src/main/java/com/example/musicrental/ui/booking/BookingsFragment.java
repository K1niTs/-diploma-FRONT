// app/src/main/java/com/example/musicrental/ui/booking/BookingsFragment.java
package com.example.musicrental.ui.booking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.example.musicrental.data.BookingDto;
import com.example.musicrental.databinding.FragmentBookingsBinding;
import com.example.musicrental.repository.BookingRepository;
import com.example.musicrental.ui.details.InstrumentDetailsFragment;
import java.util.*;
import retrofit2.*;
import com.example.musicrental.data.InstrumentDto;

public class BookingsFragment extends Fragment {

    private FragmentBookingsBinding vb;
    private final BookingRepository repo = new BookingRepository();
    private final List<BookingDto>  data = new ArrayList<>();
    private BookingAdapter          adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup c,
                             @Nullable Bundle s){
        vb = FragmentBookingsBinding.inflate(inf, c, false);
        return vb.getRoot();
    }

    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s){
        super.onViewCreated(v, s);

        adapter = new BookingAdapter(
                data,
                this::cancelBooking,
                this::handleActionClick,
                this::openInstrument
        );

        vb.rv.setLayoutManager(new LinearLayoutManager(getContext()));
        vb.rv.setAdapter(adapter);

        vb.swipe.setOnRefreshListener(this::load);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override public boolean onMove(@NonNull RecyclerView r,
                                            @NonNull RecyclerView.ViewHolder a,
                                            @NonNull RecyclerView.ViewHolder b){ return false; }
            @Override public void onSwiped(@NonNull RecyclerView.ViewHolder vh,int dir){
                cancelBooking(data.get(vh.getBindingAdapterPosition()));
            }
        }).attachToRecyclerView(vb.rv);

        load();
    }

    private void load(){
        vb.swipe.setRefreshing(true);
        repo.my(new Callback<>() {
            @Override public void onResponse(@NonNull Call<List<BookingDto>> c,
                                             @NonNull Response<List<BookingDto>> r){
                vb.swipe.setRefreshing(false);
                if (r.isSuccessful() && r.body()!=null){
                    data.clear(); data.addAll(r.body()); adapter.notifyDataSetChanged();
                } else toast("Ошибка "+r.code());
            }
            @Override public void onFailure(@NonNull Call<List<BookingDto>> c,
                                            @NonNull Throwable t){
                vb.swipe.setRefreshing(false); toast(t.getMessage());
            }
        });
    }

    private void cancelBooking(BookingDto b){
        repo.cancel(b.id, new Callback<>() {
            @Override public void onResponse(@NonNull Call<BookingDto> c,
                                             @NonNull Response<BookingDto> r){
                if (r.isSuccessful()){
                    data.remove(b); adapter.notifyDataSetChanged();
                } else toast("Не удалось отменить: "+r.code());
            }
            @Override public void onFailure(@NonNull Call<BookingDto> c,
                                            @NonNull Throwable t){
                toast(t.getMessage());
            }
        });
    }

    private void handleActionClick(BookingDto b){
        if ("WAITING_PAYMENT".equals(b.status)){
            com.example.musicrental.ui.review.AddReviewDialog
                    .newInstance(b.instrumentId)
                    .show(getParentFragmentManager(),"add_rev");
        } else {
            payBooking(b);
        }
    }

    private void payBooking(BookingDto b){
        repo.pay(b.id, new Callback<>() {
            @Override public void onResponse(@NonNull Call<BookingDto> c,
                                             @NonNull Response<BookingDto> r){
                if(r.isSuccessful() && r.body()!=null && r.body().paymentUrl!=null){
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(r.body().paymentUrl)));
                    b.status=r.body().status; b.paymentUrl=r.body().paymentUrl;
                    adapter.notifyItemChanged(data.indexOf(b));
                } else toast("Ошибка оплаты: "+r.code());
            }
            @Override public void onFailure(@NonNull Call<BookingDto> c,
                                            @NonNull Throwable t){ toast(t.getMessage()); }
        });
    }

    private static InstrumentDto toDtoFromBooking(BookingDto b) {
        return new InstrumentDto(
                b.instrumentId,
                null,
                b.instrumentTitle,
                null,
                0,
                null,
                null
        );
    }
    private void openInstrument(BookingDto b){
        InstrumentDto dto = toDtoFromBooking(b);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(((ViewGroup) requireView().getParent()).getId(),
                        InstrumentDetailsFragment.newInstance(
                                dto,
                                b.id,
                                "WAITING_PAYMENT".equals(b.status)))
                .addToBackStack(null)
                .commit();
    }
    /* ---------- misc ---------- */
    @Override public void onCreate(@Nullable Bundle s){
        super.onCreate(s);
        getParentFragmentManager().setFragmentResultListener(
                "reload_bookings", this, (k,b) -> load());
    }
    @Override public void onResume(){ super.onResume(); load(); }

    private void toast(String m){ Toast.makeText(requireContext(), m, Toast.LENGTH_SHORT).show(); }
}
