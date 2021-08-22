package com.example.harvest.crop;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import data.models.Plant;

public class CropVM extends AndroidViewModel
{
	private Plant selectedPlant;

	public CropVM(@NonNull Application application)
	{
		super(application);
	}
}
