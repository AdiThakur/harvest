package com.example.harvest.plant.edit;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.harvest.R;

import common.BaseFragment;
import common.Helper;
import common.ImageHelper;
import data.models.Plant;

public class PlantEditFragment extends BaseFragment
{
	public static final String PLANT_UID_KEY = "plant_uid";

	private PlantEditVM vm;
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
		vm = getProvider(this).get(PlantEditVM.class);
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
				vm.newImageUri = result;
				plantImage.setImageURI(vm.newImageUri);
			}
		);

		return inflater.inflate(R.layout.fragment_plant_add, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setTitle("Edit Plant");

		plantNameEditText = view.findViewById(R.id.plantAdd_plantNameEditText);
		plantUnitWeightEditText = view.findViewById(R.id.plantAdd_plantUnitWeightEditText);
		plantImage = view.findViewById(R.id.plantAdd_plantImage);
		Button chooseImage = view.findViewById(R.id.plantAdd_ChooseImageButton);
		chooseImage.setOnClickListener(v -> chooseImage());

		Bundle args = getArguments();

		if (args != null) {
			vm.getPlant$.observe(getViewLifecycleOwner(), plant -> {
				plantNameEditText.setText(plant.name);
				plantUnitWeightEditText.setText(Helper.formatUnitWeight(plant.unitWeight, false));
				plantImage.setImageBitmap(
					ImageHelper.loadBitmapFromImage(requireContext(), plant.imageFileName)
				);
			});

			long plantUid = (long) args.get(PLANT_UID_KEY);
			vm.getPlant(plantUid);
		}
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
		} else if (plantName.length() > Plant.NAME_CHARACTER_LIMIT) {
			plantNameEditText.setError(
				String.format("Name must be less than %d characters", Plant.NAME_CHARACTER_LIMIT)
			);
			return;
		}

		// Unit weight is optional
		if (!plantUnitWeightString.isEmpty()) {
			try {
				plantUnitWeight = Double.parseDouble(plantUnitWeightString);
			} catch	(NumberFormatException nfe) {
				plantUnitWeight = 0;
			}
		}

		vm.updatePlant(plantName, plantUnitWeight);
		navigateUp();
	}
}