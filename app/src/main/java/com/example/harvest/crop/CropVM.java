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

	private List<Crop> crops;
	private MutableLiveData<List<Crop>> cropsSubject;
	public LiveData<List<Crop>> cropsObservable;

	private Plant selectedPlant;
	private MutableLiveData<Plant> selectedPlantSubject;
	public LiveData<Plant> selectedPlantObservable;

	public CropVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		cropBridge = bridgeFactory.getCropBridge();

		crops = new ArrayList<>();
		cropsSubject = new MutableLiveData<>();
		cropsObservable = cropsSubject;

		selectedPlantSubject = new MutableLiveData<>();
		selectedPlantObservable = selectedPlantSubject;
	}

	public void setSelectedPlant(Plant plant)
	{
		this.selectedPlant = plant;
		selectedPlantSubject.setValue(selectedPlant);
	}

	public boolean addCrop() {
		return false;
	}

	public void getCrops()
	{
		crops.addAll(cropBridge.getAll());
		cropsSubject.setValue(crops);
	}
}
