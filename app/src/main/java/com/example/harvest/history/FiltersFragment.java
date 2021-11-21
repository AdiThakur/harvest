package com.example.harvest.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.harvest.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;
import java.util.Locale;

import common.BaseFragment;
import common.Helper;

public class FiltersFragment extends BaseFragment
{
	private final int MAX_CHIP_COUNT = 5;

	private FiltersVM filtersVM;

	private Button addSeasonFilters;
	private ChipGroup selectedSeasonsChips;

	private Button addCropFilters;
	private ChipGroup selectedCropChips;

	private TextView totalWeight;
	private TextView totalUnits;
	private TextView totalHarvests;

	private Button viewDetails;

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
		viewDetails = view.findViewById(R.id.filter_viewDetails);
		viewDetails.setOnClickListener((v) -> {});
		viewDetails.setEnabled(false);

		filtersVM.yearsMultiChoice.selected$.subscribe(selectedSeasons -> {
			populateChipGroup(selectedSeasonsChips, selectedSeasons);
			boolean enabled = selectedSeasons.size() > 0;

			if (!enabled) { selectedCropChips.removeAllViews(); }
			addCropFilters.setEnabled(enabled);
			viewDetails.setEnabled(false);
		});

		filtersVM.cropsMultiChoice.selected$.subscribe(selectedCrops -> {
			populateChipGroup(selectedCropChips, selectedCrops);
			viewDetails.setEnabled(selectedCrops.size() > 0);
		});

		filtersVM.details$.observe(getViewLifecycleOwner(), details -> {
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
}