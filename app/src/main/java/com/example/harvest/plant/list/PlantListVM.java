package com.example.harvest.plant.list;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import common.Event;
import common.Helper;
import data.bridges.BridgeFactory;
import data.bridges.PlantBridge;
import data.models.Plant;

public class PlantListVM extends AndroidViewModel
{
	private final PlantBridge plantBridge;
	private final List<Plant> plants;

	private final MutableLiveData<Boolean> addPlant;
	public LiveData<Boolean> addPlant$;

	private final MutableLiveData<Pair<Long, Integer>> deletePlant;
	public LiveData<Pair<Long, Integer>> deletePlant$;

	private final MutableLiveData<Event<String>> error;
	public LiveData<Event<String>> error$;

	public PlantListVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		plantBridge = bridgeFactory.getPlantBridge();

		plants = plantBridge.getAll();

		addPlant = new MutableLiveData<>();
		addPlant$ = addPlant;

		deletePlant = new MutableLiveData<>();
		deletePlant$ = deletePlant;

		error = new MutableLiveData<>();
		error$ = error;
	}

	public List<Plant> getPlants()
	{
		return plants;
	}

	public void addPlant(String name, double unitWeight, Uri imageUri)
	{
		String fileName = saveImage(imageUri, name);

		if (fileName == null) {
			String message = "Image wasn't saved! Please try again!";
			error.setValue(new Event<>(message));
			return;
		}

		Plant newPlant = new Plant(name, unitWeight, fileName);
		newPlant = plantBridge.insert(newPlant);

		if (newPlant.uid != 0) {
			plants.add(newPlant);
			addPlant.setValue(true);
		} else {
			String message = newPlant.name + " couldn't be saved!";
			error.setValue(new Event<>(message));
		}
	}

	public void deletePlant(Plant plant, int position)
	{
		int deleteCount = plantBridge.delete(plant);

		if (deleteCount == 0) {
			String message = plant.name + " couldn't be deleted. It is needed by 1 or more Crops!";
			error.setValue(new Event<>(message));
			return;
		}

		getApplication().getApplicationContext().deleteFile(plant.imageFileName);
		plants.remove(plant);
		deletePlant.setValue(new Pair<>(plant.uid, position));
	}

	private String saveImage(Uri imageUri, String plantName)
	{
		Context context = getApplication().getApplicationContext();
		Bitmap imageBitmap = Helper.convertImageToBitmap(context, imageUri);
		return Helper.saveBitmapToImage(context, imageBitmap, plantName);
	}
}
