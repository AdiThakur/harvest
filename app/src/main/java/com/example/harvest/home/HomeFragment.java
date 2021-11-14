package com.example.harvest.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.harvest.R;

import common.BaseFragment;

public class HomeFragment extends BaseFragment
{
	private HomeVM homeVM;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		homeVM = getProvider(this).get(HomeVM.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_home, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setTitle("Home");

		TextView currentSeason = view.findViewById(R.id.home_seasonTextView);
		currentSeason.setText(String.valueOf(homeVM.getCurrentSeason()));

		Button harvestsButton = view.findViewById(R.id.home_harvestsButton);
		harvestsButton.setOnClickListener((v) -> {
			launch(R.id.action_homeFragment_to_harvest_nav_graph);
		});

		Button cropsButton = view.findViewById(R.id.home_cropsButton);
		cropsButton.setOnClickListener((v) -> {
			launch(R.id.action_homeFragment_to_crop_nav_graph);
		});

		Button plantsButton = view.findViewById(R.id.home_plantsButton);
		plantsButton.setOnClickListener((v) -> {
			launch(R.id.action_homeFragment_to_plant_nav_graph);
		});

	}

	private void launch(@IdRes int resId)
	{
		navigateTo(R.id.homeFragment, resId);
	}

	// TODO: Update 'Selected Plant' box in CropAdd/Edit; it should only show plant image and name
	// TODO: Update 'Selected Crop' box in HarvestAdd/Edit; it should only show crop image and name

	// TODO: Create a relational hierarchy between all the entities
	// TODO: Make all bridge functions async; show a loading animation while async method executes
	// TODO: use placeholder string resources in calls to TextView.setText()
}
