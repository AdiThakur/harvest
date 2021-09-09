package com.example.harvest.plant;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.harvest.R;

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
					this.displayError("No image selected; using default");
					return;
				}
				plantAddVM.selectedImageUri = result;
				plantImage.setImageURI(plantAddVM.selectedImageUri);
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
		plantImage.setImageURI(plantAddVM.selectedImageUri);

		Button chooseImage = view.findViewById(R.id.plantAdd_ChooseImageButton);
		chooseImage.setOnClickListener(v -> chooseImage());

		Button submit = view.findViewById(R.id.plantAdd_SubmitButton);
		submit.setOnClickListener(v -> submit());

		Button cancel = view.findViewById(R.id.plantAdd_CancelButton);
		cancel.setOnClickListener(v -> cancel());
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
			plantName, Double.parseDouble(plantUnitWeight), plantAddVM.selectedImageUri
		);
		plantListVM.addPlant$.observe(getViewLifecycleOwner(), (success) -> {
			navigateUp();
		});
	}

	public void cancel()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

		builder.setMessage("Are you sure you want to cancel?");
		builder.setNegativeButton("No", (dialogInterface, i) -> {});
		builder.setPositiveButton("Yes", (dialogInterface, i) -> navigateUp());

		AlertDialog dialog = builder.create();
		dialog.show();
	}
}