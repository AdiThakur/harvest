package com.example.harvest.plant;

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

import common.Helper;
import data.bridges.BridgeFactory;
import data.bridges.PlantBridge;
import data.models.Plant;

public class PlantListVM extends AndroidViewModel
{
	private final PlantBridge plantBridge;
	private final List<Plant> plants;

	public int selectedPlantPosition;
	private final MutableLiveData<Pair<Integer, Integer>> selectedPlant;
	public final LiveData<Pair<Integer, Integer>> selectedPlant$;

	private final MutableLiveData<Boolean> addPlant;
	public LiveData<Boolean> addPlant$;

	private final MutableLiveData<Integer> deletePlant;
	public LiveData<Integer> deletePlant$;

	private final MutableLiveData<String> error;
	public LiveData<String> error$;

	public PlantListVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		plantBridge = bridgeFactory.getPlantBridge();

		plants = plantBridge.getAll();

		selectedPlantPosition = -1;
		selectedPlant = new MutableLiveData<>();
		selectedPlant$ = selectedPlant;

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
			error.setValue("Image wasn't saved! Please try again!");
			return;
		}

		Plant newPlant = new Plant(name, unitWeight, fileName);
		newPlant = plantBridge.insert(newPlant);

		if (newPlant.uid != 0) {
			plants.add(newPlant);
			addPlant.setValue(true);
		} else {
			error.setValue(newPlant.name + " couldn't be saved!");
		}
	}

	// TODO: If a Plant is selected to be added as a Crop, then that Plant is deleted, the CropAddFragment is not updated
	// TODO: Ensure that any messages passed view the error subject is only consumed ONCE
	public void deletePlant(Plant plant, int position)
	{
		int deleteCount = plantBridge.delete(plant);

		if (deleteCount == 0) {
			error.setValue(plant.name + " couldn't be deleted; it is needed by 1 or more crops!");
			return;
		}

		getApplication().getApplicationContext().deleteFile(plant.imageFileName);
		plants.remove(plant);
		deletePlant.setValue(position);
	}

	public void setSelectedPlantPosition(int position)
	{
		int oldPosition = selectedPlantPosition;
		selectedPlantPosition = position;
		selectedPlant.setValue(new Pair<>(oldPosition, position));
	}

	private String saveImage(Uri imageUri, String plantName)
	{
		Context context = getApplication().getApplicationContext();
		Bitmap imageBitmap = Helper.convertImageToBitmap(context, imageUri);
		return Helper.saveBitmapToImage(context, imageBitmap, plantName);
	}
}
