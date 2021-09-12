package com.example.harvest.harvest;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.GregorianCalendar;

import data.models.Crop;
import data.models.Plant;

public class HarvestAddVM extends AndroidViewModel
{
	private Crop crop;
	private MutableLiveData<Crop> selectedCrop;
	public LiveData<Crop> selectedCrop$;

	public GregorianCalendar storedDate;

	public HarvestAddVM(@NonNull Application application)
	{
		super(application);

		selectedCrop = new MutableLiveData<>();
		selectedCrop$ = selectedCrop;

		storedDate = new GregorianCalendar();
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
