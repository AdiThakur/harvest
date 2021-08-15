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

public class CropListVM extends AndroidViewModel
{
	private MutableLiveData<List<Crop>> crops;
	private CropBridge cropBridge;

	public CropListVM(@NonNull Application application)
	{
		super(application);

		crops = new MutableLiveData<>();
		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		cropBridge = bridgeFactory.getCropBridge();
	}

	public LiveData<List<Crop>> getCrops()
	{
		List<Crop> cropsFromDb = cropBridge.getAll();

		if (cropsFromDb != null) {
			crops.setValue(cropsFromDb);
		} else {
			crops.setValue(new ArrayList<>());
		}

		return crops;
	}
}
