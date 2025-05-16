package com.example.musicrental.ui.editor;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.musicrental.data.InstrumentDto;
import com.example.musicrental.databinding.FragmentAddEditInstrumentBinding;
import com.example.musicrental.repository.InstrumentRepository;
import com.example.musicrental.util.Prefs;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditInstrumentFragment extends Fragment {
    private static final String ARG_INSTR = "instr";

    private FragmentAddEditInstrumentBinding vb;
    private final InstrumentRepository repo = new InstrumentRepository();
    private InstrumentDto editing;

    private Uri pickedPhoto;
    private Uri cameraUri;
    private File cameraFile;
    private ActivityResultLauncher<String> permReq;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;

    public static AddEditInstrumentFragment newInstance(@Nullable InstrumentDto dto) {
        Bundle b = new Bundle();
        b.putSerializable(ARG_INSTR, dto);
        AddEditInstrumentFragment f = new AddEditInstrumentFragment();
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permReq = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), granted -> {
                    if (granted) openCamera();
                    else toast("Требуется разрешение на камеру");
                }
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        pickedPhoto = result.getData().getData();
                        Glide.with(this).load(pickedPhoto).into(vb.ivPhoto);
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(), success -> {
                    if (success && cameraUri != null) {
                        pickedPhoto = cameraUri;
                        Glide.with(this).load(pickedPhoto).into(vb.ivPhoto);
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vb = FragmentAddEditInstrumentBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editing = (InstrumentDto) getArguments().getSerializable(ARG_INSTR);
        if (editing != null) fillForm(editing);

        vb.btnPick.setOnClickListener(v -> showPickDialog());
        vb.btnSave.setOnClickListener(v -> saveInstrument());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        vb = null;
    }

    private void fillForm(InstrumentDto d) {
        vb.etTitle.setText(d.title);
        vb.etCategory.setText(d.category);
        vb.etPrice.setText(String.valueOf(d.pricePerDay));
        vb.etDesc.setText(d.description);
        if (d.imageUrl != null) Glide.with(this).load(d.imageUrl).into(vb.ivPhoto);
    }

    private boolean validate() {
        if (TextUtils.isEmpty(vb.etTitle.getText())) { toast("Введите название"); return false; }
        if (TextUtils.isEmpty(vb.etPrice.getText())) { toast("Введите цену"); return false; }
        return true;
    }

    private InstrumentDto toDto() {
        long userId = Prefs.get().getUserId();
        return new InstrumentDto(
                editing == null ? null : editing.id,
                userId,
                vb.etTitle.getText().toString().trim(),
                vb.etDesc.getText().toString().trim(),
                Double.parseDouble(vb.etPrice.getText().toString().trim()),
                vb.etCategory.getText().toString().trim(),
                null
        );
    }

    private void showPickDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Добавить фото")
                .setItems(new String[]{"С камеры", "Из галереи"}, (dialog, which) -> {
                    if (which == 0) openCamera(); else openGallery();
                })
                .show();
    }

    private void saveInstrument() {
        if (!validate()) return;
        InstrumentDto dto = toDto();
        if (editing == null) repo.addOrUpdate(dto, saveCallback);
        else repo.update(editing.id, dto, saveCallback);
    }

    private final Callback<InstrumentDto> saveCallback = new Callback<>() {
        @Override
        public void onResponse(Call<InstrumentDto> call, Response<InstrumentDto> response) {
            if (response.isSuccessful() && response.body() != null) {
                InstrumentDto saved = response.body();
                if (pickedPhoto != null) {
                    File uploadFile = cameraFile != null ? cameraFile : new File(
                            UiFileUtils.getPath(requireContext(), pickedPhoto)
                    );
                    if (uploadFile.exists()) {
                        // Загружаем фото
                        repo.uploadPhoto(saved.id, uploadFile, new Callback<>() {
                            @Override
                            public void onResponse(Call<InstrumentDto> c2, Response<InstrumentDto> r2) {
                                InstrumentDto updated = r2.body() != null ? r2.body() : saved;
                                finishOk(updated);
                            }
                            @Override
                            public void onFailure(Call<InstrumentDto> c2, Throwable t) {
                                finishOk(saved);
                            }
                        });
                    } else finishOk(saved);
                } else finishOk(saved);
            } else {
                toast("Ошибка: " + response.code());
            }
        }
        @Override
        public void onFailure(Call<InstrumentDto> call, Throwable t) {
            toast(t.getMessage());
        }
    };

    private void finishOk(InstrumentDto dto) {
        Bundle result = new Bundle();
        result.putSerializable("inst", dto);
        getParentFragmentManager().setFragmentResult("instrument_saved", result);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void toast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {
        Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(pick);
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permReq.launch(Manifest.permission.CAMERA);
            return;
        }
        cameraFile = new File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "photo_" + System.currentTimeMillis() + ".jpg"
        );
        cameraUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".provider",
                cameraFile
        );
        cameraLauncher.launch(cameraUri);
    }
}