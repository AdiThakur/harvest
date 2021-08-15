package com.example.harvest.plant;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.harvest.R;

import common.Helper;

public class PlantAddActivity extends AppCompatActivity
{
	PlantAddVM viewModel;
	ActivityResultLauncher<String> getContent;

	EditText plantNameEditText;
	EditText plantUnitWeightEditText;
	ImageView plantImage;
	Uri selectedImageUri = Uri.parse(Helper.defaultPlantImageUriString);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plant_add);

		viewModel = new ViewModelProvider(this).get(PlantAddVM.class);
	 	getContent = registerForActivityResult(
			new ActivityResultContracts.GetContent(),
			this::applySelectedImage
		);

	 	// Initialize Views
		plantNameEditText = findViewById(R.id.plantAdd_plantNameEditText);
		plantUnitWeightEditText = findViewById(R.id.plantAdd_plantUnitWeightEditText);
		plantImage = findViewById(R.id.plantAdd_plantImage);
		plantImage.setImageURI(selectedImageUri);
	}

	public void chooseImage(View view)
	{
		getContent.launch("image/*");
	}

	public void applySelectedImage(Uri result)
	{
		if (result == null) {
			Toast toast = Toast.makeText(
				this, "No image selected; using default", Toast.LENGTH_LONG
			);
			toast.show();
			return;
		}

		selectedImageUri = result;
		plantImage.setImageURI(selectedImageUri);
	}

	public void cancel(View view)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to cancel?");
		builder.setNegativeButton("No", (dialogInterface, i) -> {
			return;
		});
		builder.setPositiveButton("Yes", (dialogInterface, i) -> {
			finish();
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void submit(View view)
	{
		String plantName = plantNameEditText.getText().toString();
		String plantUnitWeight = plantUnitWeightEditText.getText().toString();

		if (plantName.isEmpty()) {
			plantNameEditText.setError("Please specify name!");
			return;
		}
		if (plantUnitWeight.isEmpty()) {
			plantUnitWeightEditText.setError("Please specify unit weight!");
			return;
		}

		boolean plantAdded = viewModel.addPlant(
			plantName,
			Double.parseDouble(plantUnitWeight),
			selectedImageUri
		);

		if (plantAdded) {
			Toast.makeText(this, "Plant added!", Toast.LENGTH_LONG).show();
			finish();
		} else {
			Toast.makeText(this, "Couldn't add plant", Toast.LENGTH_LONG).show();
		}
	}
}