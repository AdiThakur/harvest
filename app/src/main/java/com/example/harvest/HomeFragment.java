package com.example.harvest;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import common.BaseFragment;

public class HomeFragment extends BaseFragment
{
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
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

		Button harvestsButton = view.findViewById(R.id.home_harvestsButton);
		harvestsButton.setOnClickListener((v) -> {
//			launch(R.id.action_homeFragment_to_cropListFragment);
		});

		Button cropsButton = view.findViewById(R.id.home_cropsButton);
		cropsButton.setOnClickListener((v) -> {
			launch(R.id.action_homeFragment_to_cropListFragment);
		});

		// TODO: Factor out the flow of viewing a list of plants + adding new plants into its own graph resource; then it can easily be reused

		Button plantsButton = view.findViewById(R.id.home_plantsButton);
		plantsButton.setOnClickListener((v) -> {
			launch(R.id.action_homeFragment_to_plantListFragment2);
		});

	}

	private void launch(@IdRes int resId)
	{
		navigateTo(R.id.homeFragment, resId);
	}
}
