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
	private MutableLiveData<List<Harvest>> filteredResults;
	public LiveData<List<Harvest>> filteredResults$;

	// TODO: Refactor ctor
	public FiltersVM(@NonNull Application application)
	{
		super(application);

		seasonBridge = new BridgeFactory(application.getApplicationContext()).getSeasonBridge();
		harvestBridge = new BridgeFactory(application.getApplicationContext()).getHarvestBridge();
		cropBridge = new BridgeFactory(application.getApplicationContext()).getCropBridge();

		yearsMultiChoice = new MultiChoice<>();
		cropsMultiChoice = new MultiChoice<>();
		filteredResults = new MutableLiveData<>();
		filteredResults$ = filteredResults;

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
			filteredResults.setValue(filterData());
		});
	}

	public SummaryDetails summarizeData(List<Harvest> harvests)
	{
		double totalWeight = 0.0;
		int totalUnits = 0;
		int totalHarvests = harvests.size();

		for (int i = 0; i < harvests.size(); i++) {
			totalWeight += harvests.get(i).totalWeight;
			totalUnits += harvests.get(i).unitsHarvested;
		}

		return new SummaryDetails(totalWeight, totalUnits, totalHarvests);
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
