package com.example.harvest.plant;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import data.bridges.BridgeFactory;
import data.bridges.PlantBridge;
import data.models.Plant;

public class PlantListVM extends AndroidViewModel
{
	private MutableLiveData<List<Plant>> plants;
	private PlantBridge plantBridge;

	public PlantListVM(@NonNull Application application)
	{
		super(application);

		plants = new MutableLiveData<>();
		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		plantBridge = bridgeFactory.getPlantBridge();
	}

	public LiveData<List<Plant>> getPlants()
	{
		List<Plant> plantsFromDb = plantBridge.getAll();

		if (plantsFromDb != null) {
			plants.setValue(plantsFromDb);
		} else {
			plants.setValue(new ArrayList<>());
		}

		return plants;
	}
}
