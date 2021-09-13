package com.example.harvest.crop;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDate;

import data.models.Plant;

public class CropAddVM extends ViewModel
{
	private Plant selectedPlant;
	private MutableLiveData<Plant> selectedPlantSubject;
	public LiveData<Plant> selectedPlantObservable;

	public LocalDate storedDate;

	public CropAddVM()
	{
		super();
		selectedPlantSubject = new MutableLiveData<>();
		selectedPlantObservable = selectedPlantSubject;

		storedDate = LocalDate.now();
	}

	public Plant getSelectedPlant()
	{
		return this.selectedPlant;
	}

	public void setSelectedPlant(Plant plant)
	{
		this.selectedPlant = plant;
		selectedPlantSubject.setValue(selectedPlant);
	}
}
