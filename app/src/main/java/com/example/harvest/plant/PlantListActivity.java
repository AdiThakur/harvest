package com.example.harvest.plant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.harvest.R;
import com.example.harvest.crop.CropAdapter;
import com.example.harvest.crop.CropListVM;

import java.util.List;

import data.models.Crop;
import data.models.Plant;

public class PlantListActivity extends AppCompatActivity
{
	private List<Plant> plants;

	private RecyclerView recyclerView;
	private PlantAdapter adapter;
	private PlantListVM viewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plant_list);

		viewModel = new ViewModelProvider(this).get(PlantListVM.class);
		viewModel.getPlants().observe(this, newPlants -> {
			initializePlants(newPlants);
		});
	}

	private void initializePlants(List<Plant> newPlants)
	{
		plants = newPlants;
		adapter = new PlantAdapter(plants);

		recyclerView = findViewById(R.id.plantList_plantRcv);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
	}

	public void addPlant(View view)
	{
		Intent intent = new Intent(getApplicationContext(), PlantAddActivity.class);
		startActivity(intent);
	}
}