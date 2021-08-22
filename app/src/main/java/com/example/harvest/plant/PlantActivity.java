package com.example.harvest.plant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.harvest.R;

public class PlantActivity extends AppCompatActivity
{
	public static String ALLOW_MULTISELECT = "allowMultiSelect";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plant);

		// On startup, show plant list activity
		if (savedInstanceState == null) {
			Bundle bundle = new Bundle();
			bundle.putBoolean(
					ALLOW_MULTISELECT,
					getIntent().getBooleanExtra(ALLOW_MULTISELECT, false)
			);
			getSupportFragmentManager()
					.beginTransaction()
					.setReorderingAllowed(true)
					.add(R.id.plant_fragmentContainerView, PlantListFragment.class, bundle)
					.commit();
		}
	}
}