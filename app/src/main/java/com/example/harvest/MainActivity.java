package com.example.harvest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.harvest.crop.CropListFragment;
import com.example.harvest.plant.PlantListFragment;

import data.bridges.SeasonBridge;

public class MainActivity extends AppCompatActivity
{
	SeasonBridge seasonBridge;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager()
				.beginTransaction()
				.setReorderingAllowed(true)
				.add(R.id.main_fragmentContainerView, CropListFragment.class, null)
				.commit();
		}
	}
}