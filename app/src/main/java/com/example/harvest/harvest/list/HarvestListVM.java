package com.example.harvest.harvest.list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import common.BaseFragment;
import common.Event;
import data.bridges.BridgeFactory;
import data.bridges.HarvestBridge;
import data.models.Crop;
import data.models.Harvest;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import use_cases.GetCurrentSeasonIdUC;

public class HarvestListVM extends AndroidViewModel
{
	private final HarvestBridge bridge;
	private List<Harvest> harvests;

	private final MutableLiveData<List<Harvest>> loadHarvests;
	public final LiveData<List<Harvest>> loadHarvests$;

	private final MutableLiveData<Integer> deleteHarvest;
	public final LiveData<Integer> deleteHarvest$;

	private final MutableLiveData<Boolean> loading;
	public final LiveData<Boolean> loading$;

	private final MutableLiveData<Event<String>> error;
	public final LiveData<Event<String>> error$;

	public HarvestListVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		bridge = bridgeFactory.getHarvestBridge();

		loadHarvests = new MutableLiveData<>();
		loadHarvests$ = loadHarvests;

		deleteHarvest = new MutableLiveData<>();
		deleteHarvest$ = deleteHarvest;

		loading = new MutableLiveData<>();
		loading$ = loading;

		error = new MutableLiveData<>();
		error$ = error;
	}

	public void init()
	{
		loadHarvests();
	}

	private void loadHarvests()
	{
		loading.setValue(true);

		long currentSeasonId =
			(new GetCurrentSeasonIdUC(getApplication().getApplicationContext())).use();

		bridge
			.getAllBySeason(currentSeasonId)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.delay(BaseFragment.UI_DELAY_SHORT, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
			.subscribe(result -> {
				harvests = result;
				Harvest.sort(harvests);
				loadHarvests.setValue(harvests);
				loading.setValue(false);
			});
	}

	public List<Harvest> getHarvests()
	{
		return harvests;
	}

	public void addHarvest(
		int unitsHarvested, double totalWeight, LocalDate dateHarvested, Crop crop)
	{
		long currentSeasonId =
			(new GetCurrentSeasonIdUC(getApplication().getApplicationContext())).use();
		Harvest newHarvest =
			new Harvest(currentSeasonId, unitsHarvested, totalWeight, dateHarvested, crop);
		newHarvest = bridge.insert(newHarvest);

		if (newHarvest.uid == 0) {
			error.setValue(new Event<>("Couldn't add harvest!"));
		} else {
			harvests.add(newHarvest);
		}
	}

	public void deleteHarvest(Harvest harvest, int position)
	{
		int deleteCount = bridge.delete(harvest);

		if (deleteCount == 0) {
			String message = "Harvest couldn't be deleted.";
			error.setValue(new Event<>(message));
		} else {
			harvests.remove(harvest);
			deleteHarvest.setValue(position);
		}
	}
}
