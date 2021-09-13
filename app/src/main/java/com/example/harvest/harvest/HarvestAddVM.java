package com.example.harvest.harvest;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;

import data.models.Crop;

public class HarvestAddVM extends AndroidViewModel
{
	private Crop crop;
	private MutableLiveData<Crop> selectedCrop;
	public LiveData<Crop> selectedCrop$;
	public LocalDate storedDate;

	public HarvestAddVM(@NonNull Application application)
	{
		super(application);
		selectedCrop = new MutableLiveData<>();
		selectedCrop$ = selectedCrop;
		storedDate = LocalDate.now();
	}

	public Crop getSelectedCrop()
	{
		return crop;
	}

	public void setSelectedCrop(Crop crop)
	{
		this.crop = crop;
		selectedCrop.setValue(crop);
	}
}
