package com.example.harvest.plant;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import common.Helper;
import data.bridges.BridgeFactory;
import data.bridges.PlantBridge;
import data.models.Plant;

public class PlantVM extends AndroidViewModel
{
	private final PlantBridge plantBridge;

	public int selectedPlantPosition;
	private final MutableLiveData<Pair<Integer, Integer>> selectedPlantSubject;
	public final LiveData<Pair<Integer, Integer>> selectedPlantObservable;

	private final List<Plant> plants;
	private final MutableLiveData<List<Plant>> plantsSubject;
	public final LiveData<List<Plant>> plantsObservable;

	public PlantVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		plantBridge = bridgeFactory.getPlantBridge();

		selectedPlantPosition = -1;
		selectedPlantSubject = new MutableLiveData<>();
		selectedPlantObservable = selectedPlantSubject;

		plants = new ArrayList<>();
		plantsSubject = new MutableLiveData<>();
		plantsObservable = plantsSubject;
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

	public void addTestPlant()
	{
		Plant newPlant = new Plant();
		newPlant.name = "Test";
		newPlant.unitWeight = 1;
		newPlant.imageFileName = "retardo";
		plantBridge.insert(newPlant);
		plants.add(newPlant);
		plantsSubject.setValue(plants);
	}

//	public void getPlants()
//	{
//		plants.addAll(plantBridge.getAll());
//		plantsSubject.setValue(plants);
//	}

	public List<Plant> getPlants()
	{
		plants.addAll(plantBridge.getAll());
		return plants;
	}

	public boolean deletePlant(Plant plant)
	{
		int deleteCount = plantBridge.delete(plant);
		if (deleteCount > 0 && plants.remove(plant)) {
			plantsSubject.setValue(plants);
			return true;
		}

		return false;
	}

	public void setSelectedPlant(int position)
	{
		int oldPosition = selectedPlantPosition;
		selectedPlantPosition = position;
		selectedPlantSubject.setValue(new Pair<>(oldPosition, position));
	}
}
