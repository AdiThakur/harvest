package com.example.harvest.crop.edit;

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
import com.example.harvest.crop.list.CropListVM;

import java.time.LocalDate;

import common.BaseFragment;
import common.Helper;
import common.ImageHelper;
import data.models.Crop;

public class CropEditFragment extends BaseFragment
{
	private CropListVM cropListVM;
	LocalDate newPlantedDate;

	private View plantContainer;
	private ImageView plantImageView;
	private TextView plantNameTextView;
	private TextView plantWeightTextView;
	private EditText numberOfPlantsEditText;
	private CalendarView datePlantedCalendarView;

	// Lifecycle Overrides

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		cropListVM = getProvider(R.id.crop_nav_graph).get(CropListVM.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_crop_edit, container, false);
		Crop cropBeingEdited = cropListVM.getCropToUpdate();

		plantContainer = view.findViewById(R.id.cropEdit_SelectedPlantContainer);
		plantContainer.findViewById(R.id.cropEdit_noPlantSelected).setVisibility(View.GONE);
		plantContainer.findViewById(R.id.cropEdit_selectedPlantItem).setVisibility(View.VISIBLE);

		plantImageView = plantContainer.findViewById(R.id.plantRcvItem_plantImage);
		plantImageView.setImageBitmap(
			ImageHelper.loadBitmapFromImage(requireContext(), cropBeingEdited.plant.imageFileName)
		);

		plantNameTextView = plantContainer.findViewById(R.id.plantRcvItem_plantNameText);
		plantNameTextView.setText(cropBeingEdited.plant.name);

		plantWeightTextView = plantContainer.findViewById(R.id.plantRcvItem_plantUnitWeightText);
		plantWeightTextView.setText(Helper.formatUnitWeight(cropBeingEdited.plant.unitWeight));

		// Disable edit button
		plantContainer.findViewById(R.id.plantRcvItem_editPlantImageView).setVisibility(View.GONE);

		numberOfPlantsEditText = view.findViewById(R.id.cropEdit_numberOfPlantsEditText);
		numberOfPlantsEditText.setText(String.valueOf(cropBeingEdited.numberOfPlants));

		long millis = (cropBeingEdited.datePlanted.toEpochDay() + 1) * 86400 * 1000;
		datePlantedCalendarView = view.findViewById(R.id.cropEdit_datePlantedCalendarView);
		datePlantedCalendarView.setDate(millis);
		datePlantedCalendarView.setOnDateChangeListener((calendarView, year, month, day) -> {
			newPlantedDate = LocalDate.of(year, month + 1, day);
		});

		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setTitle("Edit Crop");
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

	private void submit()
	{
		Crop cropBeingEdited = cropListVM.getCropToUpdate();

		String numberOfPlantsString = numberOfPlantsEditText.getText().toString();
		LocalDate datePlanted =
				(newPlantedDate != null) ? newPlantedDate : cropBeingEdited.datePlanted;

		if (numberOfPlantsString.isEmpty()) {
			numberOfPlantsEditText.setError("Please specify an amount!");
			return;
		}

		int numberOfPlants = Integer.parseInt(numberOfPlantsString);

		cropListVM.updateCrop(numberOfPlants, datePlanted);
		navigateUp();
	}
}
