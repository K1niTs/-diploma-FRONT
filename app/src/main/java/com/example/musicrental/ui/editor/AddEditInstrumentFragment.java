package com.example.musicrental.ui.editor;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.*;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.*;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.musicrental.data.InstrumentDto;
import com.example.musicrental.databinding.FragmentAddEditInstrumentBinding;
import com.example.musicrental.repository.InstrumentRepository;
import com.example.musicrental.util.Prefs;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditInstrumentFragment extends Fragment {

    private static final String ARG_INSTR = "instr";

    public static AddEditInstrumentFragment newInstance(@Nullable InstrumentDto dto) {
        Bundle b = new Bundle();
        b.putSerializable(ARG_INSTR, dto);
        AddEditInstrumentFragment f = new AddEditInstrumentFragment();
        f.setArguments(b);
        return f;
    }

    private FragmentAddEditInstrumentBinding vb;
    private final InstrumentRepository repo = new InstrumentRepository();
    private InstrumentDto editing;
    private Uri pickedPhoto;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vb = FragmentAddEditInstrumentBinding.inflate(inf, container, false);
        return vb.getRoot();
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);
        editing = (InstrumentDto) getArguments().getSerializable(ARG_INSTR);
        if (editing != null) {
            fillForm(editing);
        }

        vb.btnPick.setOnClickListener(x -> pickImage());

        vb.btnSave.setOnClickListener(x -> {
            if (!validate()) return;
            InstrumentDto dto = toDto();

            if (editing == null) {
                repo.addOrUpdate(dto, saveCallback);
            } else {
                repo.update(editing.id, dto, saveCallback);
            }
        });
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        vb = null;
    }

    private void fillForm(InstrumentDto d) {
        vb.etTitle   .setText(d.title);
        vb.etCategory.setText(d.category);
        vb.etPrice   .setText(String.valueOf(d.pricePerDay));
        vb.etDesc    .setText(d.description);
        if (d.imageUrl != null) {
            Glide.with(this).load(d.imageUrl).into(vb.ivPhoto);
        }
    }

    private boolean validate() {
        if (TextUtils.isEmpty(vb.etTitle.getText())) {
            toast("Введите название"); return false;
        }
        if (TextUtils.isEmpty(vb.etPrice.getText())) {
            toast("Введите цену"); return false;
        }
        return true;
    }

    private InstrumentDto toDto() {
        long userId = Prefs.get().getUserId();
        return new InstrumentDto(
                editing == null ? null : editing.id,
                userId,
                vb.etTitle   .getText().toString().trim(),
                vb.etDesc    .getText().toString().trim(),
                Double.parseDouble(vb.etPrice.getText().toString().trim()),
                vb.etCategory.getText().toString().trim(),
                null
        );
    }

    private final Callback<InstrumentDto> saveCallback = new Callback<>() {
        @Override
        public void onResponse(Call<InstrumentDto> call, Response<InstrumentDto> resp) {
            if (resp.isSuccessful() && resp.body() != null) {
                InstrumentDto saved = resp.body();
                if (pickedPhoto != null) {
                    File f = new File(UiFileUtils.getPath(requireContext(), pickedPhoto));
                    repo.uploadPhoto(saved.id, f, new Callback<>() {
                        @Override public void onResponse(Call<InstrumentDto> c2, Response<InstrumentDto> r2) {
                            finishOk(saved);
                        }
                        @Override public void onFailure(Call<InstrumentDto> c2, Throwable t) {
                            finishOk(saved);
                        }
                    });
                } else {
                    finishOk(saved);
                }
            } else {
                toast("Ошибка " + resp.code());
            }
        }
        @Override
        public void onFailure(Call<InstrumentDto> call, Throwable t) {
            toast(t.getMessage());
        }
    };

    private void finishOk(InstrumentDto dto) {
        Bundle b = new Bundle();
        b.putSerializable("inst", dto);
        getParentFragmentManager()
                .setFragmentResult("instrument_saved", b);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void toast(String m) {
        Toast.makeText(requireContext(), m, Toast.LENGTH_SHORT).show();
    }

    private final ActivityResultLauncher<String> permReq =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {
                        if (granted) startPick();
                        else toast("Нет доступа к файлам");
                    });

    private final ActivityResultLauncher<Intent> imgPick =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    res -> {
                        if (res.getResultCode() == Activity.RESULT_OK && res.getData() != null) {
                            pickedPhoto = res.getData().getData();
                            Glide.with(this).load(pickedPhoto).into(vb.ivPhoto);
                        }
                    });

    private static final String PERM_MEDIA =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    ? Manifest.permission.READ_MEDIA_IMAGES
                    : Manifest.permission.READ_EXTERNAL_STORAGE;

    private void pickImage() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), PERM_MEDIA) == PackageManager.PERMISSION_GRANTED) {
            startPick();
        } else {
            permReq.launch(PERM_MEDIA);
        }
    }

    private void startPick() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imgPick.launch(i);
    }
}
