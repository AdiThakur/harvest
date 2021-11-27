package com.example.harvest.plant.edit;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

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

public class PlantEditVM extends AndroidViewModel
{
	private final PlantBridge bridge;
	private Plant toUpdate;
	public Uri newImageUri;

	private final MutableLiveData<Plant> getPlant;
	public LiveData<Plant> getPlant$;

	private final MutableLiveData<Event<String>> error;
	public LiveData<Event<String>> error$;

	public PlantEditVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		bridge = bridgeFactory.getPlantBridge();

		getPlant = new MutableLiveData<>();
		getPlant$ = getPlant;

		error = new MutableLiveData<>();
		error$ = error;
	}

	public void getPlant(long uid)
	{
		Plant plant = bridge.getById(uid);

		if (plant == null) {
			String message = "Plant couldn't be loaded";
			error.setValue(new Event<>(message));
		} else {
			toUpdate = plant;
			getPlant.setValue(plant);
		}
	}

	public void updatePlant(String name, double unitWeight)
	{
		String oldFileName = toUpdate.imageFileName;
		String fileName = toUpdate.imageFileName;
		boolean isSameImage = true;

		if (newImageUri != null) {
			String newImageName = extractFileNameFromUri(newImageUri);
			isSameImage = fileName.contains(newImageName);

			if (!isSameImage) {
				fileName = saveImage(newImageUri);
				if (fileName == null) {
					String message = "Image wasn't saved! Please try again!";
					error.setValue(new Event<>(message));
					return;
				}
			}
		}

		toUpdate.name = name;
		toUpdate.unitWeight = unitWeight;
		toUpdate.imageFileName = fileName;
		int updateCount = bridge.update(toUpdate);

		if (updateCount == 0) {
			String message = "Plant couldn't be updated!";
			deleteImage(fileName);
			error.setValue(new Event<>(message));
		} else {
			// Remove old image if this Plant is associated with a new one
			if (!isSameImage) {
				deleteImage(oldFileName);
			}
		}
	}

	// Private Helpers

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
