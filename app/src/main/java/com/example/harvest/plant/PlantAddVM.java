package com.example.harvest.plant;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import data.bridges.BridgeFactory;
import data.bridges.PlantBridge;
import data.models.Plant;

public class PlantAddVM extends AndroidViewModel
{
	private Plant plantBeingAdded;
	private PlantBridge plantBridge;

	public PlantAddVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		plantBridge = bridgeFactory.getPlantBridge();
		plantBeingAdded = new Plant();
	}

	public boolean addPlant(String name, double unitWeight, Uri imageUri)
	{
		plantBeingAdded.name = name;
		plantBeingAdded.unitWeight = unitWeight;
		plantBeingAdded.imageUri = imageUri;

		plantBeingAdded = plantBridge.insert(plantBeingAdded);
		if (plantBeingAdded != null) {
			return true;
		}

		return false;
	}
}
