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

public class PlantVM extends AndroidViewModel
{
	private final PlantBridge plantBridge;
	private final List<Plant> plants;
	public Uri selectedImageUri;

	public int selectedPlantPosition;
	private final MutableLiveData<Pair<Integer, Integer>> selectedPlant;
	public final LiveData<Pair<Integer, Integer>> selectedPlant$;

	private final MutableLiveData<Boolean> addPlant;
	public LiveData<Boolean> addPlant$;

	private final MutableLiveData<Integer> deletePlant;
	public LiveData<Integer> deletePlant$;

	private final MutableLiveData<String> saveImage;
	public LiveData<String> saveImage$;

	private final MutableLiveData<String> error;
	public LiveData<String> error$;

	// TODO: PlantVM is getting quite chonky; split it up into PlantAdd and PlantList VMs
	public PlantVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		plantBridge = bridgeFactory.getPlantBridge();

		plants = plantBridge.getAll();
		selectedImageUri = Uri.parse(Helper.defaultPlantImageUriString);

		selectedPlantPosition = -1;
		selectedPlant = new MutableLiveData<>();
		selectedPlant$ = selectedPlant;

		deletePlant = new MutableLiveData<>();
		deletePlant$ = deletePlant;

		addPlant = new MutableLiveData<>();
		addPlant$ = addPlant;

		saveImage = new MutableLiveData<>();
		saveImage$ = saveImage;

		error = new MutableLiveData<>();
		error$ = error;
	}

	public void addPlant(String name, double unitWeight, String imageFileName)
	{
		Plant newPlant = new Plant(name, unitWeight, imageFileName);
		newPlant = plantBridge.insert(newPlant);

		if (newPlant.uid != 0) {
			plants.add(newPlant);
			addPlant.setValue(true);
		} else {
			error.setValue(newPlant.name + " couldn't be saved!");
		}
	}

	public List<Plant> getPlants()
	{
		return plants;
	}

	// TODO: When deleting a plant, also delete its corresponding image from app-specific internal storage (it a non-default image was used)
	public void deletePlant(Plant plant, int position)
	{
		int deleteCount = plantBridge.delete(plant);

		if (deleteCount > 0) {
			plants.remove(plant);
			deletePlant.setValue(position);
		} else {
			String errorMessage =
				plant.name + " couldn't be deleted; it is needed by 1 or more crops!";
			error.setValue(errorMessage);
		}
	}

	public void setSelectedPlantPosition(int position)
	{
		int oldPosition = selectedPlantPosition;
		selectedPlantPosition = position;
		selectedPlant.setValue(new Pair<>(oldPosition, position));
	}

	public void saveImage(String plantName)
	{
		Context context = getApplication().getApplicationContext();
		Bitmap imageBitmap = Helper.convertImageToBitmap(context, selectedImageUri);
		String fileName = Helper.saveBitmapToImage(context, imageBitmap, plantName);

		if (fileName != null) {
			saveImage.setValue(fileName);
		} else {
			error.setValue("Image wasn't saved! Please try again!");
		}
	}
}
