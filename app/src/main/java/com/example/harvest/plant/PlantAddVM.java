package com.example.harvest.plant;

import android.net.Uri;
import androidx.lifecycle.ViewModel;

import common.Helper;

public class PlantAddVM extends ViewModel
{
	public Uri selectedImageUri;

	public PlantAddVM()
	{
		super();
		selectedImageUri = Uri.parse(Helper.defaultPlantImageUriString);
	}
}
