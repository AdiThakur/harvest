package com.example.harvest.plant.add;

import android.net.Uri;
import androidx.lifecycle.ViewModel;

import common.Helper;

public class PlantAddVM extends ViewModel
{
	private Uri selectedImageUri;

	public PlantAddVM()
	{
		super();
		selectedImageUri = Uri.parse(Helper.defaultPlantImageUriString);
	}

	public Uri getSelectedImageUri()
	{
		return selectedImageUri;
	}

	public void setSelectedImageUri(Uri selectedImageUri)
	{
		this.selectedImageUri = selectedImageUri;
	}
}
