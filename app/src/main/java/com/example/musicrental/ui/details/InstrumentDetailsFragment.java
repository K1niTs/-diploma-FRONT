package com.example.musicrental.ui.details;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.musicrental.R;
import com.example.musicrental.data.InstrumentDto;
import com.example.musicrental.data.ReviewDto;
import com.example.musicrental.databinding.FragmentInstrumentDetailsBinding;
import com.example.musicrental.repository.InstrumentRepository;
import com.example.musicrental.repository.ReviewRepository;
import com.example.musicrental.ui.booking.BookDialog;
import com.example.musicrental.ui.chat.ChatFragment;
import com.example.musicrental.ui.editor.AddEditInstrumentFragment;
import com.example.musicrental.ui.photowatch.FullscreenImageFragment;
import com.example.musicrental.ui.review.AddReviewDialog;
import com.example.musicrental.ui.review.ReviewAdapter;
import com.example.musicrental.util.Prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstrumentDetailsFragment extends Fragment {

    private static final String ARG_INST       = "instrument";
    private static final String ARG_CAN_REVIEW = "can_review";
    private static final String ARG_BOOKING_ID = "booking_id";

    private FragmentInstrumentDetailsBinding vb;
    private InstrumentDto inst;

    private final ReviewRepository     reviewRepo = new ReviewRepository();
    private final List<ReviewDto>      reviews    = new ArrayList<>();
    private ReviewAdapter              reviewAdapter;
    private final InstrumentRepository instRepo   = new InstrumentRepository();

    public static InstrumentDetailsFragment newInstance(
            InstrumentDto dto, Long bookingId, boolean canReview) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_INST, dto);
        args.putBoolean(ARG_CAN_REVIEW, canReview);
        args.putLong(ARG_BOOKING_ID, bookingId == null ? -1 : bookingId);
        InstrumentDetailsFragment f = new InstrumentDetailsFragment();
        f.setArguments(args);
        return f;
    }

    @Nullable @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        vb = FragmentInstrumentDetailsBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inst = (InstrumentDto) requireArguments().getSerializable(ARG_INST);

        vb.tvTitle   .setText(inst.title);
        vb.tvCategory.setText(inst.category);
        vb.tvPrice   .setText(String.format("%.0f ₽/день", inst.pricePerDay));
        vb.tvDesc    .setText(inst.description);

        Glide.with(this)
                .load(inst.imageUrl)
                .placeholder(R.drawable.for_add)
                .error(R.drawable.for_add)
                .into(vb.ivPhoto);

        vb.ivPhoto.setOnClickListener(v -> {
            FullscreenImageFragment
                    .newInstance(inst.imageUrl)
                    .show(getParentFragmentManager(), "fullscreen_image");
        });

        long me = Prefs.get().getUserId();
        boolean isOwner = Objects.equals(inst.ownerId, me);

        vb.btnEdit  .setVisibility(isOwner ? View.VISIBLE : View.GONE);
        vb.btnDelete.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        vb.btnBook  .setVisibility(isOwner ? View.GONE    : View.VISIBLE);
        vb.btnChat  .setVisibility(isOwner ? View.GONE    : View.VISIBLE);

        vb.btnBook.setOnClickListener(x ->
                BookDialog.newInstance(inst)
                        .show(getParentFragmentManager(), "book")
        );
        vb.btnChat.setOnClickListener(x ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                ((ViewGroup) requireView().getParent()).getId(),
                                ChatFragment.newInstance(inst.ownerId)
                        )
                        .addToBackStack(null)
                        .commit()
        );
        vb.btnEdit.setOnClickListener(x ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                ((ViewGroup) requireView().getParent()).getId(),
                                AddEditInstrumentFragment.newInstance(inst)
                        )
                        .addToBackStack(null)
                        .commit()
        );
        vb.btnDelete.setOnClickListener(x ->
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.confirm_delete)
                        .setPositiveButton(R.string.yes, (d, w) ->
                                instRepo.delete(inst.id, new Callback<Void>() {
                                    @Override public void onResponse(Call<Void> c, Response<Void> r) {
                                        if (r.isSuccessful()) {
                                            requireActivity().onBackPressed();
                                        } else {
                                            Toast.makeText(getContext(),
                                                    "Ошибка: " + r.code(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override public void onFailure(Call<Void> c, Throwable t) {
                                        Toast.makeText(getContext(),
                                                t.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                })
                        )
                        .setNegativeButton(R.string.no, null)
                        .show()
        );

        reviewAdapter = new ReviewAdapter(reviews);
        vb.rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        vb.rvReviews.setAdapter(reviewAdapter);
        loadReviews();

        boolean canReview = requireArguments().getBoolean(ARG_CAN_REVIEW, false);
        long bookingId    = requireArguments().getLong(ARG_BOOKING_ID, -1);
        vb.btnAddReview.setVisibility(canReview ? View.VISIBLE : View.GONE);
        vb.btnAddReview.setOnClickListener(x ->
                AddReviewDialog.newInstance(bookingId)
                        .show(getParentFragmentManager(), "add_rev")
        );
    }

    private void loadReviews() {
        reviewRepo.list(inst.id, new Callback<List<ReviewDto>>() {
            @Override public void onResponse(
                    Call<List<ReviewDto>> call, Response<List<ReviewDto>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    reviews.clear();
                    reviews.addAll(resp.body());
                    reviewAdapter.notifyDataSetChanged();
                }
            }
            @Override public void onFailure(
                    Call<List<ReviewDto>> call, Throwable t) {
                Toast.makeText(getContext(),
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
