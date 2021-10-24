package com.example.harvest.harvest.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.harvest.R;
import com.example.harvest.harvest.list.HarvestListVM;

import java.time.LocalDate;

import common.BaseFragment;
import common.Helper;
import data.models.Harvest;

public class HarvestEditFragment extends BaseFragment
{
	HarvestListVM harvestListVM;
	LocalDate newHarvestedDate;

	private ImageView cropImage;
	private TextView titleTextView;
	private TextView dateTextView;
	private EditText unitsHarvestedEditText, totalWeightEditText;
	private CalendarView dateHarvestCalendarView;

	// Lifecycle overrides

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		harvestListVM = getProvider(R.id.harvest_nav_graph).get(HarvestListVM.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_harvest_edit, container, false);
		Harvest harvestBeingEdited = harvestListVM.getHarvestToUpdate();

		cropImage = view.findViewById(R.id.harvestEdit_CropImageView);
		cropImage.setImageBitmap(
			Helper.loadBitmapFromImage(requireContext(), harvestBeingEdited.crop.plant.imageFileName)
		);

		titleTextView = view.findViewById(R.id.harvestEdit_TitleTextView);
		titleTextView.setText(harvestBeingEdited.crop.plant.name);

		dateTextView = view.findViewById(R.id.harvestEdit_dateTextView);
		dateTextView.setText(Helper.shortFormatOfDate(harvestBeingEdited.dateHarvested));

		unitsHarvestedEditText = view.findViewById(R.id.harvestEdit_harvestCountEditText);
		unitsHarvestedEditText.setText(String.valueOf(harvestBeingEdited.unitsHarvested));

		totalWeightEditText = view.findViewById(R.id.harvestEdit_totalWeightEditText);
		totalWeightEditText.setText(String.valueOf(harvestBeingEdited.totalWeight));

		long millis = (harvestBeingEdited.dateHarvested.toEpochDay() + 1) * 86400 * 1000;
		dateHarvestCalendarView = view.findViewById(R.id.harvestEdit_dateHarvestedCalendarView);
		dateHarvestCalendarView.setDate(millis);
		dateHarvestCalendarView.setOnDateChangeListener((calendarView, year, month, day) -> {
			newHarvestedDate = LocalDate.of(year, month + 1, day);
		});

		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setTitle("Edit Harvest");
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

	public void submit()
	{
		Harvest harvestBeingEdited = harvestListVM.getHarvestToUpdate();

		String unitsHarvestedString = unitsHarvestedEditText.getText().toString();
		String totalWeightString = totalWeightEditText.getText().toString();
		LocalDate dateHarvested =
			(newHarvestedDate != null) ? newHarvestedDate : harvestBeingEdited.dateHarvested;

		if (unitsHarvestedString.isEmpty() && totalWeightString.isEmpty()) {
			displayWarning("At least one of Units Harvested and Total Weight must be specified");
			return;
		}
		if (dateHarvested.isBefore(harvestBeingEdited.crop.datePlanted)) {
			displayWarning("Can't set date of Harvest before the Crop was planted!");
			return;
		}

		int unitsHarvested =
			(unitsHarvestedString.isEmpty()) ? 0 : Integer.parseInt(unitsHarvestedString);
		double totalWeight =
			(totalWeightString.isEmpty()) ? 0 : Double.parseDouble(totalWeightString);

		harvestListVM.updateHarvest(unitsHarvested, totalWeight, dateHarvested);
		navigateUp();
	}
}
