package com.example.harvest.history;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import data.bridges.BridgeFactory;
import data.bridges.CropBridge;
import data.bridges.HarvestBridge;
import data.bridges.SeasonBridge;
import data.models.Crop;
import data.models.Harvest;

public class FiltersVM extends AndroidViewModel
{
	private SeasonBridge seasonBridge;
	private HarvestBridge harvestBridge;
	private CropBridge cropBridge;

	public MultiChoice<Long> yearsMultiChoice;
	private List<Long> selectedYears;
	public MultiChoice<Crop> cropsMultiChoice;
	private List<Crop> selectedCrops;

	private List<Harvest> filterResult;
	private MutableLiveData<SummaryDetails> details;
	public LiveData<SummaryDetails> details$;

	// TODO: Refactor ctor
	public FiltersVM(@NonNull Application application)
	{
		super(application);

		seasonBridge = new BridgeFactory(application.getApplicationContext()).getSeasonBridge();
		harvestBridge = new BridgeFactory(application.getApplicationContext()).getHarvestBridge();
		cropBridge = new BridgeFactory(application.getApplicationContext()).getCropBridge();

		yearsMultiChoice = new MultiChoice<>();
		cropsMultiChoice = new MultiChoice<>();
		details = new MutableLiveData<>();
		details$ = details;

		yearsMultiChoice.setOptions(seasonBridge.getAllYears());
		yearsMultiChoice.selected$.subscribe(options -> {
			selectedYears = options;
			selectedCrops = new ArrayList<>();
			List<Crop> allCrops = new ArrayList<>();
			selectedYears.forEach(year -> {
				allCrops.addAll(cropBridge.getAllBySeason(year));
			});

			cropsMultiChoice.setOptions(allCrops);
		});

		cropsMultiChoice.selected$.subscribe(options -> {

			selectedCrops = options;
			List<Harvest> filteredHarvests = filterData();
			double totalWeight = 0.0;
			int totalUnitsHarvested = 0;
			int totalHarvests = filteredHarvests.size();

			for (int i = 0; i < filteredHarvests.size(); i++) {
				totalWeight += filteredHarvests.get(i).totalWeight;
				totalUnitsHarvested += filteredHarvests.get(i).unitsHarvested;
			}

			details.setValue(new SummaryDetails(totalWeight, totalUnitsHarvested, totalHarvests));
		});
	}

	public void showSeasonsMultiChoice(Context context)
	{
		yearsMultiChoice.show(context, "Select Seasons");
	}

	public void showCropsMultiChoice(Context context)
	{
		cropsMultiChoice.show(context, "Select Crops");
	}

	public List<Harvest> filterData()
	{
		List<Long> cropIds =
			selectedCrops.stream().map(crop -> crop.uid).collect(Collectors.toList());
		filterResult = harvestBridge.getAllBySeasonAndPlantIds(selectedYears, cropIds);

		return filterResult;
	}
}
