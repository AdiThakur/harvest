package com.example.harvest.harvest;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
	private final List<Harvest> harvests;

	private final MutableLiveData<Integer> deleteHarvest;
	public final LiveData<Integer> deleteHarvest$;

	private final MutableLiveData<Event<String>> error;
	public final LiveData<Event<String>> error$;

	public HarvestListVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		bridge = bridgeFactory.getHarvestBridge();
		harvests = bridge.getAll();

		deleteHarvest = new MutableLiveData<>();
		deleteHarvest$ = deleteHarvest;

		error = new MutableLiveData<>();
		error$ = error;
	}

	public List<Harvest> getHarvests()
	{
		harvests.sort((h1, h2) -> Helper.compareDates(h1.dateHarvested, h2.dateHarvested));
		return harvests;
	}

	public void addHarvest(
		int unitsHarvested, double totalWeight, LocalDate dateHarvested, Crop crop)
	{
		Optional<Harvest> matchingHarvest = harvests.stream()
			.filter(harvest ->
				(harvest.crop.uid == crop.uid) && (harvest.dateHarvested.isEqual(dateHarvested)))
			.findFirst();

		if (matchingHarvest.isPresent()) {
			updateHarvest(matchingHarvest.get(), unitsHarvested, totalWeight, dateHarvested);
			return;
		}

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

	public void updateHarvest(
		Harvest harvest, int unitsHarvested, double totalWeight, LocalDate dateHarvested)
	{
		harvest.unitsHarvested += unitsHarvested;
		harvest.totalWeight += totalWeight;
		harvest.dateHarvested = dateHarvested;

		int updateCount = bridge.update(harvest);

		if (updateCount == 0) {
			String message = "Harvest couldn't be updated.";
			error.setValue(new Event<>(message));
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
