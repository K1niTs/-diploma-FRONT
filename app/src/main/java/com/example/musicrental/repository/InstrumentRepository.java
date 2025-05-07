package com.example.musicrental.repository;

import com.example.musicrental.data.InstrumentDto;
import com.example.musicrental.model.Page;
import com.example.musicrental.network.ApiClient;
import com.example.musicrental.network.InstrumentApi;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class InstrumentRepository {

    private final InstrumentApi api =
            ApiClient.get().create(InstrumentApi.class);

    public void list(String q, String category,
                     Double minPrice, Double maxPrice,
                     int page, int size, String sort,
                     Callback<Page<InstrumentDto>> cb) {
        api.list(q, category, minPrice, maxPrice, page, size, sort)
                .enqueue(cb);
    }

    public void addOrUpdate(InstrumentDto dto,
                            Callback<InstrumentDto> cb) {
        api.add(dto).enqueue(cb);
    }

    public void update(long id,
                       InstrumentDto dto,
                       Callback<InstrumentDto> cb) {
        api.update(id, dto).enqueue(cb);
    }

    public void delete(long id,
                       Callback<Void> cb) {
        api.delete(id).enqueue(cb);
    }

    public void uploadPhoto(long id, File file,
                            Callback<InstrumentDto> cb) {
        RequestBody body = RequestBody.create(
                file, MediaType.parse("image/*")
        );
        MultipartBody.Part part = MultipartBody.Part
                .createFormData("file", file.getName(), body);
        api.uploadPhoto(id, part).enqueue(cb);
    }
}
