package com.example.mealway.screen.search.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealway.R;
import com.example.mealway.screen.search.presenter.FilterPresenter;
import com.example.mealway.screen.search.presenter.FilterPresenterImpl;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class FilterListFragment extends BottomSheetDialogFragment implements FilterView {

    private static final String ARG_TITLE = "title";
    private static final String ARG_MODE = "mode";

    private FilterPresenter presenter;
    private FilterCardAdapter adapter;
    private String title;
    private int mode;
    private OnFilterSelectedListener listener;

    // We'll keep this list temporarily to pass it to the presenter in onViewCreated
    // Alternatively, we could pass it via the constructor if we use a factory that handles MVP setup.
    private List<?> options;

    public interface OnFilterSelectedListener {
        void onFilterSelected(Object selected, int mode);
    }

    public static FilterListFragment newInstance(List<?> options, String title, int mode) {
        FilterListFragment fragment = new FilterListFragment();
        fragment.options = options;
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnFilterSelectedListener(OnFilterSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            mode = getArguments().getInt(ARG_MODE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText etSearch = view.findViewById(R.id.et_filter_search_page);
        RecyclerView rvGrid = view.findViewById(R.id.rv_filter_grid);

        adapter = new FilterCardAdapter(new ArrayList<>(), item -> {
            presenter.selectFilter(item);
        });

        rvGrid.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvGrid.setAdapter(adapter);

        presenter = new FilterPresenterImpl(this);
        presenter.init(options, mode);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.search(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void showFilters(List<?> filters) {
        adapter.setFilters((List<Object>) filters);
    }

    @Override
    public void closeFilter(Object selected, int mode) {
        if (listener != null) {
            listener.onFilterSelected(selected, mode);
        }
        dismiss();
    }
}
