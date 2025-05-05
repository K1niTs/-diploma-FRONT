// app/src/main/java/com/example/musicrental/ui/review/AddReviewDialog.java
package com.example.musicrental.ui.review;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.musicrental.R;
import com.example.musicrental.data.ReviewDto;
import com.example.musicrental.databinding.DialogAddReviewBinding;
import com.example.musicrental.repository.ReviewRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddReviewDialog extends DialogFragment {

    private static final String ARG_INSTR = "instrId";
    private DialogAddReviewBinding vb;
    private final ReviewRepository repo = new ReviewRepository();

    public static AddReviewDialog newInstance(long instrumentId) {
        Bundle b = new Bundle();
        b.putLong(ARG_INSTR, instrumentId);
        AddReviewDialog d = new AddReviewDialog();
        d.setArguments(b);
        return d;
    }

    @NonNull @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        vb = DialogAddReviewBinding.inflate(getLayoutInflater());

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(),
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog);
        builder
                .setTitle(R.string.leave_review)
                .setView(vb.getRoot())
                .setPositiveButton(R.string.send, (dialog, which) -> sendReview())
                .setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

    private void sendReview() {
        int rating     = (int) vb.sliderRating.getValue();
        String comment = vb.etComment.getText().toString().trim();
        long instrId   = requireArguments().getLong(ARG_INSTR);

        repo.add(instrId, rating, comment, new Callback<ReviewDto>() {
            @Override public void onResponse(@NonNull Call<ReviewDto> call,
                                             @NonNull Response<ReviewDto> resp) {
                if (resp.isSuccessful()) {
                    Toast.makeText(getContext(),
                            R.string.thanks_for_review,
                            Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(),
                            "Ошибка " + resp.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(@NonNull Call<ReviewDto> call,
                                            @NonNull Throwable t) {
                Toast.makeText(getContext(),
                        t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
