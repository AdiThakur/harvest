package com.example.harvest.plant;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

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

	private MutableLiveData<Integer> deletePlantSuccessSubject;
	public LiveData<Integer> deletePlantSuccess;

	private MutableLiveData<String> deletePlantErrorSubject;
	public LiveData<String> deletePlantError;

	public PlantVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		plantBridge = bridgeFactory.getPlantBridge();
		plants = plantBridge.getAll();

		selectedPlantPosition = -1;
		selectedPlantSubject = new MutableLiveData<>();
		selectedPlantObservable = selectedPlantSubject;

		deletePlantSuccessSubject = new MutableLiveData<>();
		deletePlantSuccess = deletePlantSuccessSubject;

		deletePlantErrorSubject = new MutableLiveData<>();
		deletePlantError = deletePlantErrorSubject;
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

	public void deletePlant(Plant plant, int position)
	{
		int deleteCount = plantBridge.delete(plant);

		if (deleteCount > 0) {
			plants.remove(plant);
			deletePlantSuccessSubject.setValue(position);
		} else {
			String errorMessage =
				"Plant " + plant.name + " couldn't be deleted; it is needed by 1 or more crops!";
			deletePlantErrorSubject.setValue(errorMessage);
		}
	}

	public void setSelectedPlantPosition(int position)
	{
		int oldPosition = selectedPlantPosition;
		selectedPlantPosition = position;
		selectedPlantSubject.setValue(new Pair<>(oldPosition, position));
	}
}
