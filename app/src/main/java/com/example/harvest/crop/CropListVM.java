package com.example.harvest.crop;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import data.bridges.BridgeFactory;
import data.bridges.CropBridge;
import data.models.Crop;
import data.models.Plant;

public class CropListVM extends AndroidViewModel
{
	private final CropBridge cropBridge;
	private List<Crop> crops;

	public CropListVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		cropBridge = bridgeFactory.getCropBridge();
		crops = cropBridge.getAll();
	}

	public boolean addCrop(long seasonId, LocalDateTime datePlanted, int numberOfPlants, Plant plant)
	{
		Crop crop = new Crop(seasonId, datePlanted, numberOfPlants, plant);
		cropBridge.insert(crop);

		if (crop.uid != 0) {
			crops.add(crop);
			return true;
		}

		return false;
	}

	public List<Crop> getCrops()
	{
		return crops;
	}

	public boolean deleteCrop(Crop crop)
	{
		// TODO: Add validation logic to delete query; a Crop shouldn't be deleted if it is used by one or more Harvest instances
		int deleteCount = cropBridge.delete(crop);
		return (deleteCount > 0) && (crops.remove(crop));
	}
}
