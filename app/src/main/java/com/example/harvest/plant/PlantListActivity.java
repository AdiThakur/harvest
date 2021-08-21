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
	private PlantListVM vm;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plant_list);

		// When activity loads, check if a previous VM exists
		// If it exists, load it into memory; else, create a new VM and initialize it (it will get
		// a fresh list of plants from the DB)

		vm = new ViewModelProvider(this).get(PlantListVM.class);
		subscribe(vm);
	}

	private void subscribe(PlantListVM vm)
	{
		vm.getPlants().observe(this, this::initializePlants);
	}

	private void initializePlants(List<Plant> newPlants)
	{
		plants = newPlants;
		adapter = new PlantAdapter(this, plants);

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