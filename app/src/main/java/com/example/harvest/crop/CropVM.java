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

public class CropVM extends AndroidViewModel
{
	private final CropBridge cropBridge;

	private List<Crop> crops;
	public GregorianCalendar storedDate;

	public Plant selectedPlant;
	private MutableLiveData<Plant> selectedPlantSubject;
	public LiveData<Plant> selectedPlantObservable;

	public CropVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		cropBridge = bridgeFactory.getCropBridge();
		crops = cropBridge.getAll();

		selectedPlantSubject = new MutableLiveData<>();
		selectedPlantObservable = selectedPlantSubject;
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
		int deleteCount = cropBridge.delete(crop);
		return (deleteCount > 0) && (crops.remove(crop));
	}

	public void setSelectedPlant(Plant plant)
	{
		this.selectedPlant = plant;
		selectedPlantSubject.setValue(selectedPlant);
	}
}
