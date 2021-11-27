package com.example.harvest.harvest.list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.util.List;

import common.Event;
import common.Helper;
import data.bridges.BridgeFactory;
import data.bridges.HarvestBridge;
import data.models.Crop;
import data.models.Harvest;
import use_cases.GetCurrentSeasonIdUC;

public class HarvestListVM extends AndroidViewModel
{
	private final HarvestBridge bridge;
	private List<Harvest> harvests;

	private final MutableLiveData<Integer> deleteHarvest;
	public final LiveData<Integer> deleteHarvest$;

	private final MutableLiveData<Event<String>> error;
	public final LiveData<Event<String>> error$;

	public HarvestListVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		bridge = bridgeFactory.getHarvestBridge();

		deleteHarvest = new MutableLiveData<>();
		deleteHarvest$ = deleteHarvest;

		error = new MutableLiveData<>();
		error$ = error;
	}

	public List<Harvest> loadHarvests()
	{
		long currentSeasonId =
			(new GetCurrentSeasonIdUC(getApplication().getApplicationContext())).use();
		harvests = bridge.getAllBySeason(currentSeasonId);
		sort(harvests);

		return harvests;
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

	// Private Helpers

	private void sort(List<Harvest> harvests)
	{
		harvests.sort((h1, h2) -> Helper.compareDates(h1.dateHarvested, h2.dateHarvested));
	}
}
