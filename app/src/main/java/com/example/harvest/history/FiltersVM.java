package com.example.harvest.history;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import common.BaseFragment;
import common.Event;
import common.Helper;
import data.bridges.BridgeFactory;
import data.bridges.CropBridge;
import data.bridges.HarvestBridge;
import data.bridges.SeasonBridge;
import data.models.Crop;
import data.models.Harvest;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FiltersVM extends AndroidViewModel
{
	private final SeasonBridge seasonBridge;
	private final HarvestBridge harvestBridge;
	private final CropBridge cropBridge;

	private final MultiChoice<Long> yearsMultiChoice;
	private final MultiChoice<Crop> cropsMultiChoice;

	private List<Long> selectedYearsList;
	private List<Crop> selectedCropsList;
	private List<Harvest> filterResult;
	private String backupDataCsv;

	private final MutableLiveData<List<String>> selectedYears;
	public final LiveData<List<String>> selectedYears$;

	private final MutableLiveData<List<String>> selectedCrops;
	public final LiveData<List<String>> selectedCrops$;

	private final MutableLiveData<List<Harvest>> filteredResults;
	public final LiveData<List<Harvest>> filteredResults$;

	private final MutableLiveData<Boolean> loading;
	public final LiveData<Boolean> loading$;

	private final MutableLiveData<Event<Boolean>> backupFinished;
	public final LiveData<Event<Boolean>> backupFinished$;

	private final MutableLiveData<Event<String>> error;
	public final LiveData<Event<String>> error$;

	private final CompositeDisposable subscriptions;

	public FiltersVM(@NonNull Application application)
	{
		super(application);

		seasonBridge = new BridgeFactory(application.getApplicationContext()).getSeasonBridge();
		harvestBridge = new BridgeFactory(application.getApplicationContext()).getHarvestBridge();
		cropBridge = new BridgeFactory(application.getApplicationContext()).getCropBridge();

		yearsMultiChoice = new MultiChoice<>();
		cropsMultiChoice = new MultiChoice<>();

		selectedYears = new MutableLiveData<>();
		selectedYears$ = selectedYears;

		selectedCrops = new MutableLiveData<>();
		selectedCrops$ = selectedCrops;

		filteredResults = new MutableLiveData<>();
		filteredResults$ = filteredResults;

		loading = new MutableLiveData<>();
		loading$ = loading;

		backupFinished = new MutableLiveData<>();
		backupFinished$ = backupFinished;

		error = new MutableLiveData<>();
		error$ = error;

		subscriptions = new CompositeDisposable();

		observe();
	}

	public void init()
	{
		loading.setValue(true);

		subscriptions.add(
			seasonBridge
				.getAllYears()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.delay(BaseFragment.UI_DELAY_LONG, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
				.subscribe(years -> {
					yearsMultiChoice.setOptions(years);
					filterData();
					loading.setValue(false);
				})
		);
	}

	public List<Harvest> getFilterResult()
	{
		return filterResult;
	}

	public String getBackupDataCsv()
	{
		return backupDataCsv;
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
		if (selectedYearsList != null && selectedCropsList != null) {

			loading.setValue(true);
			List<Long> cropIds =
					selectedCropsList.stream().map(crop -> crop.uid).collect(Collectors.toList());

			subscriptions.add(
				harvestBridge
					.getAllBySeasonAndPlantIds(selectedYearsList, cropIds)
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.delay(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
					.subscribe(
						harvests -> {
							filterResult = harvests;
							Harvest.sort(filterResult);
							filteredResults.setValue(filterResult);
							loading.setValue(false);
						},
						err -> {
							String payload = "There was an error in loading the Harvests!";
							loading.setValue(false);
							error.setValue(new Event<>(payload));
						}
					)
			);
		}
	}

	public void deleteHarvest(Harvest harvest)
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

	public void backupData()
	{
		if (filterResult == null || filterResult.isEmpty()) {
			backupFinished.setValue(new Event<>(true));
			error.setValue(new Event<>("No Crops selected for backup!"));
			return;
		}

		 Observable.just(filterResult)
			.observeOn(AndroidSchedulers.mainThread())
			.delay(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
			.subscribe(
				harvests -> {
					backupDataCsv = generateCsv(harvests);
					backupFinished.setValue(new Event<>(true));
					Log.println(Log.DEBUG, "backupData", backupDataCsv);
				}
			);
	}

	@Override
	protected void onCleared()
	{
		super.onCleared();
		if (!subscriptions.isDisposed()) {
			subscriptions.dispose();
		}
	}

	// Private Helpers

	private void observe()
	{
		subscriptions.add(
			yearsMultiChoice.selected$
				.subscribe(options -> {
					selectedYearsList = options;
					selectedCropsList = new ArrayList<>();
					List<Crop> allCrops = new ArrayList<>();
					selectedYearsList.forEach(year -> {
						allCrops.addAll(cropBridge.getAllBySeason(year));
					});

					cropsMultiChoice.setOptions(allCrops);
					selectedYears.setValue(Helper.allToString(selectedYearsList));
				}
			)
		);

		subscriptions.add(
			cropsMultiChoice.selected$
				.subscribe(options -> {
					selectedCropsList = options;
					selectedCrops.setValue(Crop.distinctNames(selectedCropsList));
					if (selectedCropsList.size() != 0) {
						filterData();
					}
				}
			)
		);
	}

	private String generateCsv(List<Harvest> harvests)
	{
		StringBuilder csvBuilder = new StringBuilder();
		csvBuilder.append("Season,Crop,Units,Weight\n");

		harvests.forEach(harvest -> {
			csvBuilder.append(harvest.seasonId).append(",");
			csvBuilder.append(harvest.crop.plant.name).append(",");
			csvBuilder.append(harvest.unitsHarvested).append(",");
			csvBuilder.append(harvest.totalWeight).append(",");
			csvBuilder.append("\n");
		});

		return csvBuilder.toString();
	}
}
