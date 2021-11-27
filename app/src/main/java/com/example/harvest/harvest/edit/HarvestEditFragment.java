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

import java.time.LocalDate;

import common.BaseFragment;
import common.Helper;
import common.ImageHelper;

public class HarvestEditFragment extends BaseFragment
{
	public static final String HARVEST_UID_KEY = "harvest_uid";

	private HarvestEditVM vm;

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
		vm = getProvider(this).get(HarvestEditVM.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_harvest_edit, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setTitle("Edit Harvest");

		titleTextView = view.findViewById(R.id.harvestEdit_TitleTextView);
		cropImage = view.findViewById(R.id.harvestEdit_CropImageView);
		dateTextView = view.findViewById(R.id.harvestEdit_dateTextView);
		unitsHarvestedEditText = view.findViewById(R.id.harvestEdit_harvestCountEditText);
		totalWeightEditText = view.findViewById(R.id.harvestEdit_totalWeightEditText);
		dateHarvestCalendarView = view.findViewById(R.id.harvestEdit_dateHarvestedCalendarView);
		dateHarvestCalendarView.setOnDateChangeListener((calendarView, year, month, day) -> {
			vm.newHarvestedDate = LocalDate.of(year, month + 1, day);
		});

		Bundle args = getArguments();

		if (args != null) {
			vm.getHarvest$.observe(getViewLifecycleOwner(), harvest -> {
				titleTextView.setText(harvest.crop.plant.name);
				dateTextView.setText(Helper.shortFormatOfDate(harvest.dateHarvested));
				unitsHarvestedEditText.setText(String.valueOf(harvest.unitsHarvested));
				totalWeightEditText.setText(String.valueOf(harvest.totalWeight));
				cropImage.setImageBitmap(
					ImageHelper.loadBitmapFromImage(requireContext(), harvest.crop.plant.imageFileName)
				);

				long millis = (harvest.dateHarvested.toEpochDay() + 1) * 86400 * 1000;
				dateHarvestCalendarView.setDate(millis);
			});

			long harvestUid = (long) args.get(HARVEST_UID_KEY);
			vm.getHarvest(harvestUid);
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

	public void submit()
	{
		String unitsHarvestedString = unitsHarvestedEditText.getText().toString();
		String totalWeightString = totalWeightEditText.getText().toString();

		if (unitsHarvestedString.isEmpty() && totalWeightString.isEmpty()) {
			displayWarning("At least one of Units Harvested and Total Weight must be specified");
			return;
		}
		if (vm.newHarvestedDate.isBefore(vm.toUpdate.crop.datePlanted)) {
			displayWarning("Can't set date of Harvest before the Crop was planted!");
			return;
		}

		int unitsHarvested =
			(unitsHarvestedString.isEmpty()) ? 0 : Integer.parseInt(unitsHarvestedString);
		double totalWeight =
			(totalWeightString.isEmpty()) ? 0 : Double.parseDouble(totalWeightString);

		vm.updateHarvest(unitsHarvested, totalWeight);
		navigateUp();
	}
}
