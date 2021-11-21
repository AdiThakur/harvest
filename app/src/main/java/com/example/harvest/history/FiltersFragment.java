package com.example.harvest.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harvest.R;
import com.example.harvest.harvest.HarvestAdapter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;
import java.util.Locale;

import common.BaseFragment;
import common.Helper;
import common.OnClickListener;

public class FiltersFragment extends BaseFragment implements OnClickListener {
	private final int MAX_CHIP_COUNT = 5;

	private FiltersVM filtersVM;

	private Button addSeasonFilters;
	private ChipGroup selectedSeasonsChips;

	private Button addCropFilters;
	private ChipGroup selectedCropChips;

	private TextView totalWeight;
	private TextView totalUnits;
	private TextView totalHarvests;

	private HarvestAdapter adapter;
	private RecyclerView rcv;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		filtersVM = getProvider(R.id.history_nav_graph).get(FiltersVM.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_filters, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setTitle("Apply Filters");

		addSeasonFilters = view.findViewById(R.id.filter_addSeasonFiltersButton);
		addSeasonFilters.setOnClickListener(v -> filtersVM.showSeasonsMultiChoice(getContext()));
		selectedSeasonsChips = view.findViewById(R.id.filter_selectedSeasonsChips);

		addCropFilters = view.findViewById(R.id.filter_addCropFiltersButton);
		addCropFilters.setOnClickListener(v -> filtersVM.showCropsMultiChoice(getContext()));
		addCropFilters.setEnabled(false);
		selectedCropChips = view.findViewById(R.id.filter_selectedCropsChips);

		totalWeight = view.findViewById(R.id.filter_totalWeight);
		totalUnits = view.findViewById(R.id.filter_totalUnits);
		totalHarvests = view.findViewById(R.id.filter_totalHarvests);

		rcv = view.findViewById(R.id.filter_rcv);
		rcv.setLayoutManager(new LinearLayoutManager(requireActivity()));

		filtersVM.yearsMultiChoice.selected$.subscribe(selectedSeasons -> {
			populateChipGroup(selectedSeasonsChips, selectedSeasons);
			boolean enabled = selectedSeasons.size() > 0;

			if (!enabled) { selectedCropChips.removeAllViews(); }
			addCropFilters.setEnabled(enabled);
		});

		filtersVM.cropsMultiChoice.selected$.subscribe(selectedCrops -> {
			populateChipGroup(selectedCropChips, selectedCrops);
		});

		filtersVM.filteredResults$.observe(getViewLifecycleOwner(), filteredResult -> {
			SummaryDetails details = filtersVM.summarizeData(filteredResult);
			rcv.setAdapter(new HarvestAdapter(requireContext(), filteredResult, this));
			totalWeight.setText(Helper.formatUnitWeight(details.totalWeight));
			totalUnits.setText(Helper.formatData(details.totalUnits));
			totalHarvests.setText(Helper.formatData(details.totalHarvests));
		});
	}

	private <T> void populateChipGroup(ChipGroup group, List<T> list)
	{
		int i;
		group.removeAllViews();

		for (i = 0; i < MAX_CHIP_COUNT && i < list.size(); i++) {
			Chip chip = new Chip(requireContext());
			chip.setText(list.get(i).toString());
			group.addView(chip);
		}

		if (i == MAX_CHIP_COUNT) {
			int remainingChips = list.size() - i;
			Chip ellipses = new Chip(requireContext());
			ellipses.setText(String.format(Locale.CANADA, ".. and %d more", remainingChips));
			group.addView(ellipses);
		}
	}

	@Override
	public void onClick(View row, int rowIndex) {}

	@Override
	public void onLongClick(View row, int rowIndex) {}

	@Override
	public void onNestedButtonClick(int rowIndex) {}
}