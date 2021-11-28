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
import com.example.harvest.harvest.edit.HarvestEditFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import common.BaseFragment;
import common.Helper;
import common.OnClickListener;
import data.models.Harvest;

public class FiltersFragment extends BaseFragment implements OnClickListener
{
	private final int MAX_CHIP_COUNT = 5;

	private FiltersVM vm;

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
		vm = getProvider(R.id.history_nav_graph).get(FiltersVM.class);
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
		addSeasonFilters.setOnClickListener(v -> vm.showSeasonsMultiChoice(getContext()));
		selectedSeasonsChips = view.findViewById(R.id.filter_selectedSeasonsChips);

		addCropFilters = view.findViewById(R.id.filter_addCropFiltersButton);
		addCropFilters.setOnClickListener(v -> vm.showCropsMultiChoice(getContext()));
		addCropFilters.setEnabled(false);
		selectedCropChips = view.findViewById(R.id.filter_selectedCropsChips);

		totalWeight = view.findViewById(R.id.filter_totalWeight);
		totalUnits = view.findViewById(R.id.filter_totalUnits);
		totalHarvests = view.findViewById(R.id.filter_totalHarvests);

		rcv = view.findViewById(R.id.filter_rcv);
		rcv.setLayoutManager(new LinearLayoutManager(requireActivity()));

		// Initial render of selected options
		if (vm.getSelectedYears() != null) {
			populateChipGroup(selectedSeasonsChips, vm.getSelectedYears());
			addCropFilters.setEnabled(true);
		}
		if (vm.getSelectedCrops() != null) {
			populateChipGroup(selectedCropChips, vm.getSelectedCrops());
		}

		observe();
		vm.filterData();
	}

	private void observe()
	{
		vm.yearsMultiChoice.selected$.subscribe(selectedSeasons -> {
			populateChipGroup(selectedSeasonsChips, selectedSeasons);
			boolean isEmpty = selectedSeasons.size() == 0;

			if (isEmpty) {
				selectedCropChips.removeAllViews();
				clearSummaryAndDetails();
			}

			addCropFilters.setEnabled(!isEmpty);
		});

		vm.cropsMultiChoice.selected$.subscribe(selectedCrops -> {
			populateChipGroup(selectedCropChips, selectedCrops);
			boolean isEmpty = selectedCrops.size() == 0;

			if (isEmpty) {
				clearSummaryAndDetails();
			}
		});

		vm.filteredResults$.observe(getViewLifecycleOwner(), filteredResult -> {
			if (filteredResult != null) {
				SummaryDetails details = vm.summarizeData(filteredResult);
				rcv.setAdapter(new HarvestAdapter(requireContext(), filteredResult, this));
				totalWeight.setText(Helper.formatUnitWeight(details.totalWeight));
				totalUnits.setText(Helper.formatData(details.totalUnits));
				totalHarvests.setText(Helper.formatData(details.totalHarvests));
			}
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

	private void clearSummaryAndDetails()
	{
		rcv.setAdapter(new HarvestAdapter(requireContext(), new ArrayList<>(), this));
		totalWeight.setText(Helper.formatUnitWeight(0));
		totalUnits.setText(Helper.formatData(0));
		totalHarvests.setText(Helper.formatData(0));
	}

	// OnClickListener interface overrides for HarvestAdapter

	@Override
	public void onClick(View row, int position) {}

	@Override
	public void onLongClick(View row, int position) {}

	@Override
	public void onNestedButtonClick(int position)
	{
		Harvest harvestToEdit = vm.getFilterResult().get(position);
		Bundle bundle = new Bundle();
		bundle.putLong(HarvestEditFragment.HARVEST_UID_KEY, harvestToEdit.uid);

		navigateTo(
			R.id.filtersFragment,
			R.id.action_filtersFragment_to_harvestEditFragment2,
			bundle
		);
	}
}