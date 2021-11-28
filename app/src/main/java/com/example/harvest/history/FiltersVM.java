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

import common.Event;
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

	private final MutableLiveData<Event<String>> error;
	public final LiveData<Event<String>> error$;

	public FiltersVM(@NonNull Application application)
	{
		super(application);

		seasonBridge = new BridgeFactory(application.getApplicationContext()).getSeasonBridge();
		harvestBridge = new BridgeFactory(application.getApplicationContext()).getHarvestBridge();
		cropBridge = new BridgeFactory(application.getApplicationContext()).getCropBridge();

		yearsMultiChoice = new MultiChoice<>();
		yearsMultiChoice.setOptions(seasonBridge.getAllYears());

		cropsMultiChoice = new MultiChoice<>();
		filteredResults = new MutableLiveData<>();
		filteredResults$ = filteredResults;

		error = new MutableLiveData<>();
		error$ = error;

		observe();
	}

	public List<Harvest> getFilterResult()
	{
		return filterResult;
	}

	public List<Long> getSelectedYears()
	{
		return selectedYears;
	}

	public List<Crop> getSelectedCrops()
	{
		return selectedCrops;
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

	public void filterData()
	{
		if (selectedYears != null && selectedCrops != null) {
			List<Long> cropIds =
					selectedCrops.stream().map(crop -> crop.uid).collect(Collectors.toList());
			filterResult = harvestBridge.getAllBySeasonAndPlantIds(selectedYears, cropIds);

			filteredResults.setValue(filterResult);
		}
	}

	public void deleteHarvest(Harvest harvest, int position)
	{
		int deleteCount = harvestBridge.delete(harvest);

		if (deleteCount == 0) {
			String message = "Harvest couldn't be deleted.";
			error.setValue(new Event<>(message));
		} else {
			filterResult.remove(harvest);
			filteredResults.setValue(filterResult);
		}
	}


	// Private Helpers

	private void observe()
	{
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
			filterData();
		});
	}
}
