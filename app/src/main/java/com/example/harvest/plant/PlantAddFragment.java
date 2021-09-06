package com.example.harvest.plant;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.harvest.R;

import common.BaseFragment;
import common.Helper;

public class PlantAddFragment extends BaseFragment
{
	private PlantVM plantVM;
	private ActivityResultLauncher<String> getContent;

	private EditText plantNameEditText;
	private EditText plantUnitWeightEditText;
	private ImageView plantImage;
	private Uri selectedImageUri = Uri.parse(Helper.defaultPlantImageUriString);

	// Lifecycle Overrides

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		plantVM = getProvider(R.id.plant_nav_graph).get(PlantVM.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		getContent = registerForActivityResult(
			new ActivityResultContracts.GetContent(), this::applySelectedImage
		);

		return inflater.inflate(R.layout.fragment_plant_add, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setTitle("Add a New Plant");

		// Initialize Views
		plantNameEditText = view.findViewById(R.id.plantAdd_plantNameEditText);
		plantUnitWeightEditText = view.findViewById(R.id.plantAdd_plantUnitWeightEditText);
		plantImage = view.findViewById(R.id.plantAdd_plantImage);
		plantImage.setImageURI(selectedImageUri);

		Button chooseImage = view.findViewById(R.id.plantAdd_ChooseImageButton);
		chooseImage.setOnClickListener(v -> { chooseImage(); });

		Button submit = view.findViewById(R.id.plantAdd_SubmitButton);
		submit.setOnClickListener(v -> { submit(); });

		Button cancel = view.findViewById(R.id.plantAdd_CancelButton);
		cancel.setOnClickListener(v -> { cancel(); });
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

		// Copy selected image to internal storage; on failure, abort saving plant to DB
		String savedFileName = copyImageToInternalStorage(selectedImageUri, plantName);
		if (savedFileName == null) {
			Toast.makeText(
				requireActivity(),
				"Image wasn't saved! Please try again.",
				Toast.LENGTH_SHORT
			).show();
			return;
		}

		boolean plantAdded = plantVM.addPlant(
			plantName,
			Double.parseDouble(plantUnitWeight),
			savedFileName
		);

		if (plantAdded) {
			navigateUp();
			return;
		}

		Toast.makeText(
			requireActivity(),
			"Couldn't add plant",
			Toast.LENGTH_LONG
		).show();
	}

	public void cancel()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

		builder.setMessage("Are you sure you want to cancel?");
		builder.setNegativeButton("No", (dialogInterface, i) -> {});
		builder.setPositiveButton("Yes", (dialogInterface, i) -> {
			navigateUp();
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	// Helpers

	private void applySelectedImage(Uri result)
	{
		if (result == null) {
			Toast toast = Toast.makeText(
				requireActivity(), "No image selected; using default", Toast.LENGTH_LONG
			);
			toast.show();
			return;
		}

		selectedImageUri = result;
		plantImage.setImageURI(selectedImageUri);
	}

	private String copyImageToInternalStorage(Uri imageUri, String plantName)
	{
		Bitmap imageBitmap = Helper.convertImageToBitmap(requireActivity(), imageUri);
		return Helper.saveBitmapToImage(requireActivity(), imageBitmap, plantName);
	}
}