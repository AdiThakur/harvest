package com.example.harvest.crop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harvest.R;

import java.time.LocalDateTime;
import java.util.GregorianCalendar;

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
		cropAddVM = new ViewModelProvider(getStoreOwner(R.id.add_crop_graph)).get(CropAddVM.class);
		// TODO: Update CropListVM so that it is scoped to a nested graph, and not the activity
		cropListVM = (new ViewModelProvider(requireActivity())).get(CropListVM.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_crop_add, container, false);
		plantContainer = view.findViewById(R.id.cropAdd_SelectedPlantContainer);
		plantContainer.setOnClickListener((v) -> launchCropListFragment());

		plantImageView = plantContainer.findViewById(R.id.plantRcvItem_plantImage);
		plantNameTextView = plantContainer.findViewById(R.id.plantRcvItem_plantNameText);
		plantWeightTextView = plantContainer.findViewById(R.id.plantRcvItem_plantUnitWeightText);
		numberOfPlantsEditText = view.findViewById(R.id.addCrop_numberOfPlantsEditText);

		datePlantedCalendarView = view.findViewById(R.id.addCrop_datePlantedCalendarView);
		datePlantedCalendarView.setOnDateChangeListener((calendarView, year, month, day) -> {
			cropAddVM.storedDate = new GregorianCalendar(year, month, day);
		});

		submitButton = view.findViewById(R.id.addCrop_submitButton);
		submitButton.setOnClickListener((v) -> submit());
		submitButton.setEnabled(false);

		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setTitle("Add a New Crop");

		cropAddVM.selectedPlantObservable.observe(getViewLifecycleOwner(), selectedPlant ->
		{
			plantContainer.findViewById(R.id.noPlantSelected).setVisibility(View.GONE);
			plantContainer.findViewById(R.id.selectedPlantItem).setVisibility(View.VISIBLE);

			plantNameTextView.setText(selectedPlant.name);
			plantWeightTextView.setText(String.valueOf(selectedPlant.unitWeight));
			plantImageView.setImageBitmap(
					Helper.loadBitmapFromImage(requireContext(), selectedPlant.imageFileName)
			);

			submitButton.setEnabled(true);
		});
	}

	// Callbacks for user-generated events

	private void launchCropListFragment()
	{
		NavHostFragment.findNavController(this).navigate(R.id.select_plant_graph);
	}

	private void  submit()
	{
		Plant selectedPlant = cropAddVM.getSelectedPlant();
		String numberOfPlants = numberOfPlantsEditText.getText().toString();
		LocalDateTime datePlanted = cropAddVM.storedDate.toZonedDateTime().toLocalDateTime();

		if (numberOfPlants.isEmpty()) {
			numberOfPlantsEditText.setError("Please specify an amount!");
		}

		boolean cropAdded = cropListVM.addCrop(
			// TODO: Replace hardcoded season
			2021,
			datePlanted,
			Integer.parseInt(numberOfPlants),
			selectedPlant
		);

		if (cropAdded) {
			navigateUp();
			return;
		}

		Toast.makeText(
			requireActivity(),
			"Couldn't add crop",
			Toast.LENGTH_LONG
		).show();
	}
}
