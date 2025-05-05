// app/src/main/java/com/example/musicrental/ui/catalog/InstrumentListFragment.java
package com.example.musicrental.ui.catalog;

import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.*;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.musicrental.R;
import com.example.musicrental.data.InstrumentDto;
import com.example.musicrental.databinding.FragmentInstrumentListBinding;
import com.example.musicrental.model.Page;
import com.example.musicrental.repository.InstrumentRepository;

import java.util.*;
import retrofit2.*;

public class InstrumentListFragment extends Fragment {

    /* ---------- data ---------- */
    private FragmentInstrumentListBinding vb;
    private final InstrumentRepository     repo   = new InstrumentRepository();
    private final List<InstrumentDto>      data   = new ArrayList<>();
    private InstrumentAdapter              adapter;

    private int page = 0, totalPages = 1;
    private final FilterState filters = new FilterState();

    public InstrumentListFragment() { setHasOptionsMenu(true); }

    /* ---------- life-cycle ---------- */

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup container,
                             @Nullable Bundle saved) {

        vb = FragmentInstrumentListBinding.inflate(inf, container, false);

        adapter = new InstrumentAdapter(data, this::openDetails);
        vb.rv.setLayoutManager(new LinearLayoutManager(getContext()));
        vb.rv.setAdapter(adapter);

        // endless-scroll
        vb.rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(@NonNull RecyclerView rv,int dx,int dy) {
                if (!rv.canScrollVertically(1) && page < totalPages - 1) {
                    page++; loadPage();
                }
            }
        });

        // FAB «добавить»
        vb.fabAdd.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(((ViewGroup) requireView().getParent()).getId(),
                                com.example.musicrental.ui.editor.AddEditInstrumentFragment
                                        .newInstance(null))
                        .addToBackStack(null)
                        .commit());

        /* Слушаем результат из экрана создания/редактирования */
        getParentFragmentManager().setFragmentResultListener(
                "instrument_saved", this, (k, bundle) -> {
                    InstrumentDto upd =
                            (InstrumentDto) bundle.getSerializable("inst");

                    int idx = findById(upd.id);
                    if (idx >= 0) {             // редактировали существующий
                        data.set(idx, upd);
                        adapter.notifyItemChanged(idx);
                    } else {                    // создали новый
                        data.add(0, upd);
                        adapter.notifyItemInserted(0);
                        vb.rv.scrollToPosition(0);
                    }
                });

        if (saved == null) loadFirstPage();
        return vb.getRoot();
    }

    @Override public void onDestroyView() { super.onDestroyView(); vb = null; }

    /* ---------- options-menu ---------- */

    @Override public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater i) {
        i.inflate(R.menu.menu_catalog, menu);

        SearchView sv = (SearchView) menu.findItem(R.id.action_search).getActionView();
        sv.setQueryHint(getString(R.string.search));
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String q) {
                filters.query = q; loadFirstPage(); sv.clearFocus(); return true;
            }
            @Override public boolean onQueryTextChange(String q){ return false; }
        });
    }

    @Override public boolean
    onOptionsItemSelected(@NonNull MenuItem it) {
        if (it.getItemId() == R.id.action_filters) {
            new FilterSheet(filters, f -> { filters.copyFrom(f); loadFirstPage(); })
                    .show(getParentFragmentManager(),"flt");
            return true;
        } else if (it.getItemId() == R.id.action_profile) {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(((ViewGroup) requireView().getParent()).getId(),
                            new com.example.musicrental.ui.profile.ProfileFragment())
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(it);

    }

    /* ---------- networking ---------- */

    private void loadFirstPage() {
        data.clear(); page = 0; totalPages = 1; adapter.notifyDataSetChanged();
        loadPage();
    }

    private void loadPage() {
        repo.list(filters.query, filters.category,
                filters.minPrice, filters.maxPrice,
                page, 10, filters.orderBy,
                new Callback<Page<InstrumentDto>>() {

                    @Override public void onResponse(@NonNull Call<Page<InstrumentDto>> c,
                                                     @NonNull Response<Page<InstrumentDto>> r) {
                        if (r.isSuccessful() && r.body() != null) {
                            Page<InstrumentDto> p = r.body();
                            totalPages = p.totalPages;
                            data.addAll(p.content);
                            adapter.notifyDataSetChanged();
                        } else toast("Ошибка: "+r.code());
                    }
                    @Override public void onFailure(@NonNull Call<Page<InstrumentDto>> c,
                                                    @NonNull Throwable t) { toast(t.getMessage()); }
                });
    }

    /* ---------- helpers ---------- */

    private int findById(long id){
        for(int i=0;i<data.size();i++) if(data.get(i).id==id) return i;
        return -1;
    }

    private void openDetails(InstrumentDto dto){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(((ViewGroup) requireView().getParent()).getId(),
                        com.example.musicrental.ui.details.InstrumentDetailsFragment
                                .newInstance(dto, null, false)) // <-- 3 аргумента
                .addToBackStack(null)
                .commit();
    }

    private void toast(String m){
        Toast.makeText(requireContext(), m, Toast.LENGTH_SHORT).show();
    }
}
