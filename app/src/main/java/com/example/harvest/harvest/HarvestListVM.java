package com.example.harvest.harvest;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import data.bridges.BridgeFactory;
import data.bridges.HarvestBridge;
import data.models.Harvest;

public class HarvestListVM extends AndroidViewModel
{
	private final HarvestBridge bridge;
	private List<Harvest> harvests;

	public HarvestListVM(@NonNull Application application)
	{
		super(application);

		BridgeFactory bridgeFactory = new BridgeFactory(application.getApplicationContext());
		bridge = bridgeFactory.getHarvestBridge();
		harvests = bridge.getAll();
	}

	public List<Harvest> getHarvests()
	{
		return harvests;
	}
}
