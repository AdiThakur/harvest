package com.example.harvest.harvest;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import common.Event;
import common.Helper;
import data.bridges.BridgeFactory;
import data.bridges.HarvestBridge;
import data.models.Crop;
import data.models.Harvest;

public class HarvestListVM extends AndroidViewModel
{
	private final HarvestBridge bridge;
	private List<Harvest> harvests;

	private final MutableLiveData<Integer> deleteHarvest;
	public final LiveData<Integer> deleteHarvest$;

	private final MutableLiveData<Event<String>> error;
	public LiveData<Event<String>> error$;

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
		return harvests;
	}

	// TODO: If two harvests for the same crop are made on the same day, do not create a new harvest object; instead, add the new info to the old harvest object
	public void addHarvest(
		int unitsHarvested, double totalWeight, LocalDateTime dateHarvested, Crop crop)
	{
		Optional<Harvest> matchingHarvest = harvests.stream()
			.filter(harvest -> (
				(harvest.crop.uid == crop.uid)	&&
				(Helper.compareDates(dateHarvested, crop.datePlanted) == 0)))
			.findFirst();
		
		if (matchingHarvest.isPresent()) {
			updateHarvest(matchingHarvest.get(), unitsHarvested, totalWeight, dateHarvested);
			return;
		}

		// TODO: Get rid of hardcoded season id
		Harvest newHarvest =
			new Harvest(2021, unitsHarvested, totalWeight, dateHarvested, crop);
		newHarvest = bridge.insert(newHarvest);

		if (newHarvest.uid != 0) {
			harvests.add(newHarvest);
		} else {
			error.setValue(new Event<>("Couldn't add harvest!"));
		}
	}

	public void updateHarvest(
		Harvest harvest, int unitsHarvested, double totalWeight, LocalDateTime dateHarvested)
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
			return;
		}

		harvests.remove(harvest);
		deleteHarvest.setValue(position);
	}
}
