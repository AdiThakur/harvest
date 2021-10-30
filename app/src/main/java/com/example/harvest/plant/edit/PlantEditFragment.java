package com.example.harvest.plant.edit;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.harvest.R;
import com.example.harvest.plant.list.PlantListVM;

import common.BaseFragment;
import common.Helper;
import data.models.Plant;

public class PlantEditFragment extends BaseFragment
{
	Uri newImageUri;

	private PlantListVM plantListVM;
	private ActivityResultLauncher<String> getContent;

	private EditText plantNameEditText;
	private EditText plantUnitWeightEditText;
	private ImageView plantImage;

	// Lifecycle Overrides

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		plantListVM = getProvider(R.id.plant_nav_graph).get(PlantListVM.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		getContent = registerForActivityResult(
			new ActivityResultContracts.GetContent(),
			(Uri result) -> {
				if (result == null) {
					displayWarning("No image selected; using default");
					return;
				}
				newImageUri = result;
				plantImage.setImageURI(newImageUri);
			}
		);

		return inflater.inflate(R.layout.fragment_plant_add, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setTitle("Edit Plant");

		Plant plantBeingEdited = plantListVM.getPlantToUpdate();

		plantNameEditText = view.findViewById(R.id.plantAdd_plantNameEditText);
		plantNameEditText.setText(plantBeingEdited.name);

		plantUnitWeightEditText = view.findViewById(R.id.plantAdd_plantUnitWeightEditText);
		plantUnitWeightEditText.setText(String.valueOf(plantBeingEdited.unitWeight));

		plantImage = view.findViewById(R.id.plantAdd_plantImage);
		plantImage.setImageBitmap(
			Helper.loadBitmapFromImage(requireContext(), plantBeingEdited.imageFileName)
		);

		Button chooseImage = view.findViewById(R.id.plantAdd_ChooseImageButton);
		chooseImage.setOnClickListener(v -> chooseImage());
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.save_menu_option, menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		int id = item.getItemId();

		if (id == R.id.saveMenuOption_SaveButton) {
			submit();
		}

		return true;
	}

	// Callbacks for user-generated events

	public void chooseImage()
	{
		getContent.launch("image/*");
	}

	public void submit()
	{
		String plantName = plantNameEditText.getText().toString();
		String plantUnitWeightString = plantUnitWeightEditText.getText().toString();
		double plantUnitWeight = 0;

		if (plantName.isEmpty()) {
			plantNameEditText.setError("Please specify name!");
			return;
		}

		// Unit weight is optional
		if (!plantUnitWeightString.isEmpty()) {
			plantUnitWeight = Double.parseDouble(plantUnitWeightString);
		}

		plantListVM.updatePlant(plantName, plantUnitWeight, newImageUri);
		navigateUp();
	}
}