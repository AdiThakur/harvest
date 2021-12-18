package com.example.harvest.harvest.edit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;

import common.Event;
import data.bridges.BridgeFactory;
import data.bridges.HarvestBridge;
import data.models.Harvest;

public class HarvestEditVM extends AndroidViewModel
{
	private final HarvestBridge bridge;
	public Harvest toUpdate;
	public LocalDate newHarvestedDate;

	private final MutableLiveData<Harvest> getHarvest;
	public LiveData<Harvest> getHarvest$;

	private final MutableLiveData<Event<String>> error;
	public final LiveData<Event<String>> error$;

	public HarvestEditVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		bridge = bridgeFactory.getHarvestBridge();

		getHarvest = new MutableLiveData<>();
		getHarvest$ = getHarvest;

		error = new MutableLiveData<>();
		error$ = error;
	}

	public void getHarvest(long uid)
	{
		Harvest harvest = bridge.get(uid);

		if (harvest == null) {
			String message = "Harvest couldn't be loaded";
			error.setValue(new Event<>(message));
		} else {
			toUpdate = harvest;
			newHarvestedDate = toUpdate.dateHarvested;
			getHarvest.setValue(harvest);
		}
	}

	public void updateHarvest(int unitsHarvested, double totalWeight)
	{
		toUpdate.unitsHarvested = unitsHarvested;
		toUpdate.totalWeight = totalWeight;
		toUpdate.dateHarvested = newHarvestedDate;

		int updateCount = bridge.update(toUpdate);

		if (updateCount == 0) {
			String message = "Harvest couldn't be updated!";
			error.setValue(new Event<>(message));
		}
	}
}
