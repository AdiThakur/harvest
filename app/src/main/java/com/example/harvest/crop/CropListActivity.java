package com.example.harvest.crop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.harvest.R;

import java.util.List;

import data.models.Crop;

public class CropListActivity extends AppCompatActivity
{
	private List<Crop> crops;

	private RecyclerView recyclerView;
	private CropAdapter adapter;
	private CropListVM viewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop_list);

		// Setup AddCropViewModel and observers
		viewModel = new ViewModelProvider(this).get(CropListVM.class);
		viewModel.getCrops().observe(this, newCrops -> {
			initializeCrops(newCrops);
		});
	}

	private void initializeCrops(List<Crop> newCrops)
	{
		crops = newCrops;
		adapter = new CropAdapter(crops);

		recyclerView = findViewById(R.id.cropList_cropRcv);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
	}
}