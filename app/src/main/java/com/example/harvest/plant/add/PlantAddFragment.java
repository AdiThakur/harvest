package com.example.harvest.plant.add;

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

public class PlantAddFragment extends BaseFragment
{
	private PlantAddVM plantAddVM;
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
		plantAddVM = getProvider(this).get(PlantAddVM.class);
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
				plantAddVM.setSelectedImageUri(result);
				plantImage.setImageURI(plantAddVM.getSelectedImageUri());
			}
		);

		return inflater.inflate(R.layout.fragment_plant_add, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setTitle("Add a New Plant");

		plantNameEditText = view.findViewById(R.id.plantAdd_plantNameEditText);
		plantUnitWeightEditText = view.findViewById(R.id.plantAdd_plantUnitWeightEditText);
		plantImage = view.findViewById(R.id.plantAdd_plantImage);
		plantImage.setImageURI(plantAddVM.getSelectedImageUri());

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
		String plantUnitWeight = plantUnitWeightEditText.getText().toString();

		if (plantName.isEmpty()) {
			plantNameEditText.setError("Please specify name!");
			return;
		}
		if (plantUnitWeight.isEmpty()) {
			plantUnitWeightEditText.setError("Please specify unit weight!");
			return;
		}

		plantListVM.addPlant(
			plantName, Double.parseDouble(plantUnitWeight), plantAddVM.getSelectedImageUri()
		);
		plantListVM.addPlant$.observe(getViewLifecycleOwner(), (success) -> {
			navigateUp();
		});
	}
}