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

	private final List<Plant> plants;

	public int selectedPlantPosition;
	private final MutableLiveData<Pair<Integer, Integer>> selectedPlantSubject;
	public final LiveData<Pair<Integer, Integer>> selectedPlantObservable;

	public PlantVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		plantBridge = bridgeFactory.getPlantBridge();
		plants = plantBridge.getAll();

		selectedPlantPosition = -1;
		selectedPlantSubject = new MutableLiveData<>();
		selectedPlantObservable = selectedPlantSubject;
	}

	public boolean addPlant(String name, double unitWeight, String imageFileName)
	{
		Plant newPlant = new Plant(name, unitWeight, imageFileName);
		newPlant = plantBridge.insert(newPlant);

		if (newPlant.uid != 0) {
			plants.add(newPlant);
			return true;
		}

		return false;
	}

	public List<Plant> getPlants()
	{
		return plants;
	}

	public boolean deletePlant(Plant plant)
	{
		int deleteCount = plantBridge.delete(plant);
		return (deleteCount > 0) && (plants.remove(plant));
	}

	public void setSelectedPlant(int position)
	{
		int oldPosition = selectedPlantPosition;
		selectedPlantPosition = position;
		selectedPlantSubject.setValue(new Pair<>(oldPosition, position));
	}
}
