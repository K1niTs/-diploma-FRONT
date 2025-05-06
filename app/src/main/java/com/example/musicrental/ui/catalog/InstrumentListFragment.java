package com.example.musicrental.ui.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicrental.R;
import com.example.musicrental.data.InstrumentDto;
import com.example.musicrental.databinding.FragmentInstrumentListBinding;
import com.example.musicrental.model.Page;
import com.example.musicrental.repository.InstrumentRepository;
import com.example.musicrental.ui.editor.AddEditInstrumentFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstrumentListFragment extends Fragment {

    private FragmentInstrumentListBinding vb;
    private final InstrumentRepository repo = new InstrumentRepository();
    private final List<InstrumentDto> data = new ArrayList<>();
    private InstrumentAdapter adapter;

    private int page = 0;
    private int totalPages = 1;
    private final FilterState filters = new FilterState();

    public InstrumentListFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vb = FragmentInstrumentListBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view,
                                        @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new InstrumentAdapter(data, this::openDetails);
        vb.rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        vb.rv.setAdapter(adapter);

        vb.swipe.setOnRefreshListener(this::loadFirstPage);

        vb.rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                if (!rv.canScrollVertically(1) && page < totalPages - 1) {
                    page++;
                    loadPage();
                }
            }
        });

        vb.fabAdd.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(((ViewGroup) requireView().getParent()).getId(),
                                AddEditInstrumentFragment.newInstance(null))
                        .addToBackStack(null)
                        .commit()
        );

        getParentFragmentManager().setFragmentResultListener(
                "instrument_saved", this,
                (requestKey, bundle) -> {
                    InstrumentDto upd = (InstrumentDto) bundle.getSerializable("inst");
                    int idx = findById(upd.id);
                    if (idx >= 0) {
                        data.set(idx, upd);
                        adapter.notifyItemChanged(idx);
                    } else {
                        data.add(0, upd);
                        adapter.notifyItemInserted(0);
                        vb.rv.scrollToPosition(0);
                    }
                }
        );

        if (savedInstanceState == null) {
            loadFirstPage();
        }
    }

    @Override public void onCreateOptionsMenu(@NonNull Menu menu,
                                              @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_catalog, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView sv = (SearchView) searchItem.getActionView();
        sv.setQueryHint(getString(R.string.search));

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                filters.query = query.trim();
                loadFirstPage();
                sv.clearFocus();
                return true;
            }
            @Override public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.trim().isEmpty()) {
                    filters.query = null;
                    loadFirstPage();
                    return true;
                }
                return false;
            }
        });

        // сбрасываем поиск при сворачивании SearchView
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
            @Override public boolean onMenuItemActionCollapse(MenuItem item) {
                filters.query = null;
                loadFirstPage();
                return true;
            }
        });
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_filters) {
            new FilterSheet(filters, f -> {
                filters.copyFrom(f);
                loadFirstPage();
            }).show(getParentFragmentManager(), "flt");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFirstPage() {
        vb.swipe.setRefreshing(true);
        data.clear();
        page = 0;
        totalPages = 1;
        adapter.notifyDataSetChanged();
        loadPage();
    }

    private void loadPage() {
        repo.list(
                filters.query,
                filters.category,
                filters.minPrice,
                filters.maxPrice,
                page,
                10,
                filters.orderBy,
                new Callback<Page<InstrumentDto>>() {
                    @Override public void onResponse(Call<Page<InstrumentDto>> call,
                                                     Response<Page<InstrumentDto>> resp) {
                        vb.swipe.setRefreshing(false);
                        if (resp.isSuccessful() && resp.body() != null) {
                            totalPages = resp.body().totalPages;
                            data.addAll(resp.body().content);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(requireContext(),
                                    "Ошибка: " + resp.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(Call<Page<InstrumentDto>> call, Throwable t) {
                        vb.swipe.setRefreshing(false);
                        Toast.makeText(requireContext(),
                                t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private int findById(long id) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).id == id) return i;
        }
        return -1;
    }

    private void openDetails(InstrumentDto dto) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(((ViewGroup) requireView().getParent()).getId(),
                        com.example.musicrental.ui.details.InstrumentDetailsFragment
                                .newInstance(dto, null, false))
                .addToBackStack(null)
                .commit();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        vb = null;
    }
}
