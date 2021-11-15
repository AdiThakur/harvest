package com.example.harvest.crop.list;

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
	private final CropBridge bridge;
	private final List<Crop> crops;

	private final MutableLiveData<Pair<Long, Integer>> deleteCrop;
	public final LiveData<Pair<Long, Integer>> deleteCrop$;

	private Crop toUpdate;

	private final MutableLiveData<Event<String>> error;
	public final LiveData<Event<String>> error$;

	public CropListVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		long currentSeasonId = (new GetCurrentSeasonIdUC(application.getApplicationContext())).use();
		bridge = bridgeFactory.getCropBridge();
		crops = bridge.getAllBySeason(currentSeasonId);

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
		bridge.insert(crop);

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
		int deleteCount = bridge.delete(crop);

		if (deleteCount == 0) {
			String message =
				crop.plant.name + " couldn't be deleted. It is needed by 1 or more Harvests!";
			error.setValue(new Event<>(message));
		} else {
			crops.remove(crop);
			deleteCrop.setValue(new Pair<>(crop.uid, position));
		}
	}

	public Crop getCropToUpdate()
	{
		return toUpdate;
	}

	public void setCropToUpdate(int position)
	{
		toUpdate = crops.get(position);
	}

	public void updateCrop(int numberOfPlants, LocalDate datePlanted)
	{
		Crop updateCopy = Crop.ShallowCopy(toUpdate);
		updateCopy.numberOfPlants = numberOfPlants;
		updateCopy.datePlanted = datePlanted;

		int updateCount = bridge.update(updateCopy);

		if (updateCount == 0) {
			String message = "Crop couldn't be updated!";
			error.setValue(new Event<>(message));
		} else {
			toUpdate.numberOfPlants = numberOfPlants;
			toUpdate.datePlanted = datePlanted;
		}
	}
}
