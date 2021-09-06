package com.example.harvest.crop;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.GregorianCalendar;

import data.models.Plant;

public class CropAddVM extends ViewModel
{
	private Plant selectedPlant;
	private MutableLiveData<Plant> selectedPlantSubject;
	public LiveData<Plant> selectedPlantObservable;
	public GregorianCalendar storedDate = new GregorianCalendar();

	public CropAddVM()
	{
		super();
		selectedPlantSubject = new MutableLiveData<>();
		selectedPlantObservable = selectedPlantSubject;
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
