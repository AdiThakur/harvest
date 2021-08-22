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

public class PlantVM extends AndroidViewModel
{
	private final PlantBridge plantBridge;
	private final List<Plant> plants;
	private MutableLiveData<List<Plant>> plantsSubject;

	public PlantVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		plantBridge = bridgeFactory.getPlantBridge();

		plants = new ArrayList<>();
		plants.addAll(plantBridge.getAll());
	}

	public boolean addPlant(String name, double unitWeight, String imageFileName)
	{
		Plant newPlant = new Plant();
		newPlant.name = name;
		newPlant.unitWeight = unitWeight;
		newPlant.imageFileName = imageFileName;

		newPlant = plantBridge.insert(newPlant);
		if (newPlant.uid != 0) {
			plants.add(newPlant);
			plantsSubject.setValue(plants);
			return true;
		}

		return false;
	}

	public LiveData<List<Plant>> lookupPlants()
	{
		if (plantsSubject == null) {
			plantsSubject = new MutableLiveData<>();
		}

		plantsSubject.setValue(plants);
		return plantsSubject;
	}
}
