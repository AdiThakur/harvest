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
	private final PlantBridge bridge;
	private final List<Plant> plants;

	private final MutableLiveData<Pair<Long, Integer>> deletePlant;
	public LiveData<Pair<Long, Integer>> deletePlant$;

	private Plant toUpdate;

	private final MutableLiveData<Event<String>> error;
	public LiveData<Event<String>> error$;

	public PlantListVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		bridge = bridgeFactory.getPlantBridge();

		plants = bridge.getAll();

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
		String fileName = saveImage(imageUri);

		if (fileName == null) {
			String message = "Image wasn't saved! Please try again!";
			error.setValue(new Event<>(message));
			return;
		}

		Plant newPlant = new Plant(name, unitWeight, fileName);
		newPlant = bridge.insert(newPlant);

		if (newPlant.uid == 0) {
			String message = newPlant.name + " couldn't be saved!";
			deleteImage(newPlant.imageFileName);
			error.setValue(new Event<>(message));
		} else {
			plants.add(newPlant);
		}
	}

	public void deletePlant(Plant plant, int position)
	{
		int deleteCount = bridge.delete(plant);

		if (deleteCount == 0) {
			String message = plant.name + " couldn't be deleted. It is needed by 1 or more Crops!";
			error.setValue(new Event<>(message));
		} else {
			deleteImage(plant.imageFileName);
			plants.remove(plant);
			deletePlant.setValue(new Pair<>(plant.uid, position));
		}
	}

	public Plant getPlantToUpdate()
	{
		return toUpdate;
	}

	public void setPlantToUpdate(int position)
	{
		toUpdate = plants.get(position);
	}

	public void updatePlant(String name, double unitWeight, Uri imageUri)
	{
		String fileName = toUpdate.imageFileName;
		boolean isSameImage = true;

		if (imageUri != null) {
			String newImageName = extractFileNameFromUri(imageUri);
		 	isSameImage= fileName.contains(newImageName);

			if (!isSameImage) {
				fileName = saveImage(imageUri);
				if (fileName == null) {
					String message = "Image wasn't saved! Please try again!";
					error.setValue(new Event<>(message));
					return;
				}
			}
		}

		Plant updateCopy = Plant.ShallowCopy(toUpdate);
		updateCopy.name = name;
		updateCopy.unitWeight = unitWeight;
		updateCopy.imageFileName = fileName;

		int updateCount = bridge.update(updateCopy);

		if (updateCount == 0) {
			String message = "Plant couldn't be updated!";
			deleteImage(fileName);
			error.setValue(new Event<>(message));
		} else {
			// Remove old image
			if (!isSameImage) {
				deleteImage(toUpdate.imageFileName);
			}

			toUpdate.name = name;
			toUpdate.unitWeight = unitWeight;
			toUpdate.imageFileName = fileName;
		}
	}

	private String extractFileNameFromUri(Uri uri)
	{
		List<String> segments = uri.getPathSegments();
		return segments.get(segments.size() - 1);
	}

	private String saveImage(Uri imageUri)
	{
		Context context = getApplication().getApplicationContext();
		Bitmap imageBitmap = Helper.convertImageToBitmap(context, imageUri);

		return Helper.saveBitmapToImage(context, imageBitmap, extractFileNameFromUri(imageUri));
	}

	private boolean deleteImage(String fileName)
	{
		return getApplication().getApplicationContext().deleteFile(fileName);
	}
}
