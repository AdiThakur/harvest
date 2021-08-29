package com.example.harvest.crop;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import data.bridges.BridgeFactory;
import data.bridges.CropBridge;
import data.models.Crop;
import data.models.Plant;

public class CropVM extends AndroidViewModel
{
	private final CropBridge cropBridge;
	private final List<Crop> crops;
	private MutableLiveData<List<Crop>> cropsSubject;

	public CropVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		cropBridge = bridgeFactory.getCropBridge();

		crops = new ArrayList<>();
		crops.addAll(cropBridge.getAll());
	}

	public boolean addCrop() {
		return false;
	}

	public LiveData<List<Crop>> lookupCrops()
	{
		if (cropsSubject == null) {
			cropsSubject = new MutableLiveData<>();
		}

		cropsSubject.setValue(crops);
		return cropsSubject;
	}
}
