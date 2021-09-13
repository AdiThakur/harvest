package com.example.harvest.crop;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.util.List;

import common.Event;
import data.bridges.BridgeFactory;
import data.bridges.CropBridge;
import data.models.Crop;
import data.models.Plant;

public class CropListVM extends AndroidViewModel
{
	private final CropBridge cropBridge;
	private List<Crop> crops;

	private final MutableLiveData<Pair<Long, Integer>> deleteCrop;
	public LiveData<Pair<Long, Integer>> deleteCrop$;

	private final MutableLiveData<Event<String>> error;
	public LiveData<Event<String>> error$;

	public CropListVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		cropBridge = bridgeFactory.getCropBridge();
		// TODO: get rid of hardcoded season (Also add seasonal logic to home screen and harvests list)
		crops = cropBridge.getAllBySeason(2021);

		deleteCrop = new MutableLiveData<>();
		deleteCrop$ = deleteCrop;

		error = new MutableLiveData<>();
		error$ = error;
	}

	public void addCrop(long seasonId, LocalDate datePlanted, int numberOfPlants, Plant plant)
	{
		boolean cropExistsForPlant = crops.stream().anyMatch(crop -> crop.plantId == plant.uid);

		if (cropExistsForPlant) {
			String message = plant.name + " already has a Crop associated to it!";
			error.setValue(new Event<>(message));
			return;
		}

		Crop crop = new Crop(seasonId, datePlanted, numberOfPlants, plant);
		cropBridge.insert(crop);

		if (crop.uid != 0) {
			crops.add(crop);
		} else {
			error.setValue(new Event<>("Couldn't add " + plant.name));
		}
	}

	public List<Crop> getCrops()
	{
		return crops;
	}

	public void deleteCrop(Crop crop, int position)
	{
		int deleteCount = cropBridge.delete(crop);

		if (deleteCount == 0) {
			String message =
				crop.plant.name + " couldn't be deleted. It is needed by 1 or more Harvests!";
			error.setValue(new Event<>(message));
			return;
		}

		crops.remove(crop);
		deleteCrop.setValue(new Pair<>(crop.uid, position));
	}
}
