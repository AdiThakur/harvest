package com.example.harvest.harvest.add;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.harvest.R;
import com.example.harvest.harvest.list.HarvestListVM;

import java.time.LocalDate;

import common.BaseFragment;
import common.Helper;
import common.ImageHelper;
import data.models.Crop;

public class HarvestAddFragment extends BaseFragment
{
	private HarvestAddVM harvestAddVM;
	private HarvestListVM harvestListVM;

	private View cropContainer;
	private ImageView cropImageView;
	private TextView cropNameTextView, cropCountTextView, cropPlantedDateTextView;
	private EditText unitsHarvestedEditText, totalWeightEditText;
	private CalendarView dateHarvestCalendarView;

	// Lifecycle Overrides

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		harvestListVM = getProvider(R.id.harvest_nav_graph).get(HarvestListVM.class);
		harvestAddVM = getProvider(R.id.harvest_add_nav_graph).get(HarvestAddVM.class);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_harvest_add, container, false);
		cropContainer = view.findViewById(R.id.harvestAdd_selectedCropContainer);
		cropContainer.setOnClickListener((v) -> launchCropListFragment());

		cropImageView = cropContainer.findViewById(R.id.cropRcvItem_cropImage);
		cropNameTextView = cropContainer.findViewById(R.id.cropRcvItem_cropNameText);
		cropCountTextView = cropContainer.findViewById(R.id.cropRcvItem_cropCountText);
		cropPlantedDateTextView = cropContainer.findViewById(R.id.cropRcvItem_cropDatePlantedtext);

		unitsHarvestedEditText = view.findViewById(R.id.harvestAdd_harvestCountEditText);
		totalWeightEditText = view.findViewById(R.id.harvestAdd_totalWeightEditText);

		dateHarvestCalendarView = view.findViewById(R.id.harvestAdd_dateHarvestedCalendarView);
		dateHarvestCalendarView.setOnDateChangeListener((calendarView, year, month, day) -> {
			harvestAddVM.storedDate = LocalDate.of(year, month + 1, day);
		});

		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setTitle("New Harvest");

		harvestAddVM.selectedCrop$.observe(getViewLifecycleOwner(), (Crop selectedCrop) ->
		{
			if (selectedCrop == null) {
				cropContainer.findViewById(R.id.noPlantSelected).setVisibility(View.VISIBLE);
				cropContainer.findViewById(R.id.selectedCropItem).setVisibility(View.GONE);
			} else {
				cropContainer.findViewById(R.id.noPlantSelected).setVisibility(View.GONE);
				cropContainer.findViewById(R.id.selectedCropItem).setVisibility(View.VISIBLE);
				cropNameTextView.setText(selectedCrop.plant.name);
				cropCountTextView.setText(String.valueOf(selectedCrop.numberOfPlants));
				cropImageView.setImageBitmap(
					ImageHelper.loadBitmapFromImage(requireContext(), selectedCrop.plant.imageFileName)
				);
				cropPlantedDateTextView.setText(Helper.shortFormatOfDate(selectedCrop.datePlanted));
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.save_menu_option, menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		int id = item.getItemId();

		if (id == R.id.saveMenuOption_SaveButton) {
			submit();
		}

		return true;

	}

	// Callbacks for user-generated events

	private void launchCropListFragment()
	{
		navigateTo(R.id.harvestAddFragment, R.id.action_harvestAddFragment_to_crop_nav_graph);
	}

	private void submit()
	{
		Crop selectedCrop = harvestAddVM.getSelectedCrop();

		String unitsHarvestedString = unitsHarvestedEditText.getText().toString();
		String totalWeightString = totalWeightEditText.getText().toString();
		LocalDate dateHarvested = harvestAddVM.storedDate;

		if (selectedCrop == null) {
			displayWarning("Please select a Crop!");
			return;
		}
		if (unitsHarvestedString.isEmpty() && totalWeightString.isEmpty()) {
			displayWarning("At least one of Units Harvested and Total Weight must be specified");
			return;
		}
		if (dateHarvested.isBefore(selectedCrop.datePlanted)) {
			displayWarning("Can't set date of Harvest before the Crop was planted!");
			return;
		}

		int unitsHarvested =
			(unitsHarvestedString.isEmpty()) ? 0 : Integer.parseInt(unitsHarvestedString);
		double totalWeight =
			(totalWeightString.isEmpty()) ? 0 : Double.parseDouble(totalWeightString);

		harvestListVM.addHarvest(unitsHarvested, totalWeight, dateHarvested, selectedCrop);
		navigateUp();
	}
}