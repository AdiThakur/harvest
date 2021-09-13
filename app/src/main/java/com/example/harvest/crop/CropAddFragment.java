package com.example.harvest.crop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.harvest.R;

import java.time.LocalDate;

import common.BaseFragment;
import common.Helper;
import data.models.Plant;

public class CropAddFragment extends BaseFragment
{
	private CropAddVM cropAddVM;
	private CropListVM cropListVM;

	private View plantContainer;
	private ImageView plantImageView;
	private TextView plantNameTextView;
	private TextView plantWeightTextView;
	private EditText numberOfPlantsEditText;
	private CalendarView datePlantedCalendarView;
	private Button submitButton;

	// Lifecycle Overrides

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		cropAddVM = getProvider(R.id.crop_add_graph).get(CropAddVM.class);
		cropListVM = getProvider(R.id.crop_nav_graph).get(CropListVM.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_crop_add, container, false);
		plantContainer = view.findViewById(R.id.cropAdd_SelectedPlantContainer);
		plantContainer.setOnClickListener((v) -> launchPlantListFragment());

		plantImageView = plantContainer.findViewById(R.id.plantRcvItem_plantImage);
		plantNameTextView = plantContainer.findViewById(R.id.plantRcvItem_plantNameText);
		plantWeightTextView = plantContainer.findViewById(R.id.plantRcvItem_plantUnitWeightText);
		numberOfPlantsEditText = view.findViewById(R.id.addCrop_numberOfPlantsEditText);

		datePlantedCalendarView = view.findViewById(R.id.addCrop_datePlantedCalendarView);
		datePlantedCalendarView.setOnDateChangeListener((calendarView, year, month, day) -> {
			cropAddVM.storedDate = LocalDate.of(year, month + 1, day);
		});

		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setTitle("New Crop");

		cropAddVM.selectedPlantObservable.observe(getViewLifecycleOwner(), selectedPlant ->
		{
			if (selectedPlant == null) {
				plantContainer.findViewById(R.id.noPlantSelected).setVisibility(View.VISIBLE);
				plantContainer.findViewById(R.id.selectedPlantItem).setVisibility(View.GONE);
			} else {
				plantContainer.findViewById(R.id.noPlantSelected).setVisibility(View.GONE);
				plantContainer.findViewById(R.id.selectedPlantItem).setVisibility(View.VISIBLE);
				plantNameTextView.setText(selectedPlant.name);
				plantWeightTextView.setText(String.valueOf(selectedPlant.unitWeight));
				plantImageView.setImageBitmap(
					Helper.loadBitmapFromImage(requireContext(), selectedPlant.imageFileName)
				);
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.crop_add_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		int id = item.getItemId();

		if (id == R.id.crop_add_menu_saveButton) {
			submit();
		}

		return true;
	}

	// Callbacks for user-generated events

	private void launchPlantListFragment()
	{
		navigateTo(R.id.cropAddFragment, R.id.action_cropAddFragment_to_plant_nav_graph);
	}

	private void submit()
	{
		Plant selectedPlant = cropAddVM.getSelectedPlant();
		String numberOfPlantsString = numberOfPlantsEditText.getText().toString();
		LocalDate datePlanted = cropAddVM.storedDate;

		if (selectedPlant == null) {
			displayWarning("Please select a Plant!");
			return;
		}
		if (numberOfPlantsString.isEmpty()) {
			numberOfPlantsEditText.setError("Please specify an amount!");
			return;
		}

		int numberOfPlants = Integer.parseInt(numberOfPlantsString);

		// TODO: Replace hardcoded season
		cropListVM.addCrop(2021, datePlanted, numberOfPlants, selectedPlant);
		navigateUp();
	}
}
