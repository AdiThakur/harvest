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
import use_cases.GetCurrentSeasonIdUC;

public class CropListVM extends AndroidViewModel
{
	private final CropBridge cropBridge;
	private final List<Crop> crops;

	private final MutableLiveData<Pair<Long, Integer>> deleteCrop;
	public final LiveData<Pair<Long, Integer>> deleteCrop$;

	private final MutableLiveData<Event<String>> error;
	public final LiveData<Event<String>> error$;

	public CropListVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		cropBridge = bridgeFactory.getCropBridge();
		long currentSeasonId = (new GetCurrentSeasonIdUC(application.getApplicationContext())).use();
		crops = cropBridge.getAllBySeason(currentSeasonId);

		deleteCrop = new MutableLiveData<>();
		deleteCrop$ = deleteCrop;

		error = new MutableLiveData<>();
		error$ = error;
	}

	public void addCrop(LocalDate datePlanted, int numberOfPlants, Plant plant)
	{
		boolean cropExistsForPlant = crops.stream().anyMatch(crop -> crop.plantId == plant.uid);

		if (cropExistsForPlant) {
			String message = plant.name + " already has a Crop associated to it!";
			error.setValue(new Event<>(message));
			return;
		}

		long seasonId = (new GetCurrentSeasonIdUC(getApplication().getApplicationContext())).use();
		Crop crop = new Crop(seasonId, datePlanted, numberOfPlants, plant);
		cropBridge.insert(crop);

		if (crop.uid == 0) {
			error.setValue(new Event<>("Couldn't add " + plant.name));
		} else {
			crops.add(crop);
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
		} else {
			crops.remove(crop);
			deleteCrop.setValue(new Pair<>(crop.uid, position));
		}
	}
}
