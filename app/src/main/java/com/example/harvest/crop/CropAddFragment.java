package com.example.harvest.crop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.harvest.R;

import common.BaseFragment;
import common.Helper;

public class CropAddFragment extends BaseFragment
{
	private CropVM cropVM;

	View plantContainer;
	ImageView plantImageView;
	TextView plantNameTextView;
	TextView plantWeightTextView;
	EditText numberOfPlantsEditText;
	CalendarView datePlantedCalendarView;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		cropVM = new ViewModelProvider(getStoreOwner(R.id.add_crop_graph)).get(CropVM.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_crop_add, container, false);
		plantContainer = view.findViewById(R.id.cropAdd_SelectedPlantContainer);
		plantContainer.setOnClickListener((v) -> {
			launchCropListFragment();
		});

		plantImageView = plantContainer.findViewById(R.id.plantRcvItem_plantImage);
		plantNameTextView = plantContainer.findViewById(R.id.plantRcvItem_plantNameText);
		plantWeightTextView = plantContainer.findViewById(R.id.plantRcvItem_plantUnitWeightText);
		numberOfPlantsEditText = view.findViewById(R.id.addCrop_numberOfPlantsEditText);
		datePlantedCalendarView = view.findViewById(R.id.addCrop_datePlantedCalendarView);

		return view;
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setTitle("Add a New Crop");

		cropVM.selectedPlantObservable.observe(getViewLifecycleOwner(), selectedPlant -> {
			plantImageView.setImageBitmap(
				Helper.loadBitmapFromImage(requireContext(), selectedPlant.imageFileName)
			);
			plantNameTextView.setText(selectedPlant.name);
			plantWeightTextView.setText(Double.toString(selectedPlant.unitWeight));
		});
	}

	public void launchCropListFragment()
	{
		NavHostFragment.findNavController(this).navigate(R.id.select_plant_graph);
	}
}