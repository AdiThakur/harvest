package com.example.harvest.crop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.harvest.R;
import com.example.harvest.plant.PlantListFragment;

public class CropActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop);

		// On startup, show CropList fragment
		if (savedInstanceState == null) {
			getSupportFragmentManager()
				.beginTransaction()
				.setReorderingAllowed(true)
				.add(R.id.crop_fragmentContainerView, CropListFragment.class, null)
				.commit();
		}
	}
}