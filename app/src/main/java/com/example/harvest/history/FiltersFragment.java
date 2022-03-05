package com.example.harvest.history;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

	private RecyclerView rcv;
	private AlertDialog step2Dialog;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
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
		setTitle("History");

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

		observe();
		vm.init();
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.backup_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		int id = item.getItemId();

		if (id == R.id.backupMenu_BackupButton) {
			showBackupDialogStep1();
		}

		return true;
	}

	private void observe()
	{
		vm.loading$.observe(getViewLifecycleOwner(), isLoading -> {
			showLoadingDialog(isLoading);
		});

		vm.selectedYears$.observe(getViewLifecycleOwner(), years -> {
			populateChipGroup(selectedSeasonsChips, years);
			boolean isEmpty = years.size() == 0;

			if (isEmpty) {
				selectedCropChips.removeAllViews();
				clearSummaryAndDetails();
			}

			addCropFilters.setEnabled(!isEmpty);
		});

		vm.selectedCrops$.observe(getViewLifecycleOwner(), crops -> {
			populateChipGroup(selectedCropChips, crops);
			if (crops.size() == 0) {
				clearSummaryAndDetails();
			}
		});

		vm.filteredResults$.observe(getViewLifecycleOwner(), filteredResult -> {
			if (filteredResult != null) {
				SummaryDetails details = vm.summarizeData(filteredResult);
				rcv.setAdapter(new HarvestAdapter(requireContext(), filteredResult, this));
				totalWeight.setText(Helper.formatUnitWeight(details.totalWeight, true));
				totalUnits.setText(Helper.formatData(details.totalUnits));
				totalHarvests.setText(Helper.formatData(details.totalHarvests));
			}
		});

		vm.backupFinished$.observe(getViewLifecycleOwner(), booleanEvent -> {
			if (booleanEvent.isFreshPiece()) {
				booleanEvent.consume();
				step2Dialog.dismiss();
				showBackupDialogStep3();
			}
		});

		vm.error$.observe(getViewLifecycleOwner(), error -> {
			displayError(getView(), error);
		});
	}

	private void populateChipGroup(ChipGroup group, List<String> list)
	{
		int i;
		group.removeAllViews();

		for (i = 0; i < MAX_CHIP_COUNT && i < list.size(); i++) {
			Chip chip = new Chip(requireContext());
			chip.setText(list.get(i));
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
		totalWeight.setText(Helper.formatUnitWeight(0, true));
		totalUnits.setText(Helper.formatData(0));
		totalHarvests.setText(Helper.formatData(0));
	}

	private void showBackupDialogStep1()
	{
		if (vm.getFilterResult() == null) {
			displayWarning("Please select some Crops first!");
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
		LayoutInflater inflater = getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.backup_dialog_step1, null);
		builder.setView(dialogView);
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", (d, i) -> showBackupDialogStep2());
		builder.setNegativeButton("Cancel", (d, i) -> { return; });

		builder.create().show();
	}

	private void showBackupDialogStep2()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
		LayoutInflater inflater = getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.backup_dialog_step2, null);
		builder.setView(dialogView);
		builder.setCancelable(false);

		step2Dialog = builder.create();
		step2Dialog.show();

		vm.backupData();
	}

	private void showBackupDialogStep3()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
		LayoutInflater inflater = getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.backup_dialog_step3, null);
		builder.setView(dialogView);
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", (d, i) -> emailData());
		builder.setNegativeButton("No", (d, i) -> { return; });

		builder.create().show();
	}

	private void emailData()
	{
		// TODO: Launch an email intent with the newly created file as its sole attachtment
	}

	// OnClickListener interface overrides for HarvestAdapter

	@Override
	public void onClick(View row, int position) {}

	@Override
	public void onLongClick(View row, int position)
	{
		Harvest harvestToDelete = vm.getFilterResult().get(position);
		AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

		builder.setTitle("Delete Harvest?");
		String message =
			"Information about the Harvest of " + harvestToDelete.crop.plant.name +
			" on " + Helper.shortFormatOfDate(harvestToDelete.dateHarvested) + " will be lost!";
		builder.setMessage(message);
		builder.setNegativeButton("No", (dialogInterface, i) -> {});
		builder.setPositiveButton("Yes", (dialogInterface, i) ->
			vm.deleteHarvest(harvestToDelete)
		);

		AlertDialog dialog = builder.create();
		dialog.show();
	}

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