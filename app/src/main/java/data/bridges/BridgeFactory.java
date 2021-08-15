package data.bridges;

import android.content.Context;

import data.db.HarvestDB;

public class BridgeFactory
{
	private HarvestDB db;

	public BridgeFactory(Context context)
	{
		this.db = HarvestDB.getInstance(context);
	}

	public PlantBridge getPlantBridge()
	{
		return new PlantBridge(db.plantDao());
	}

	public CropBridge getCropBridge()
	{
		return new CropBridge(db.cropDao(), getPlantBridge());
	}

	public HarvestBridge getHarvestBridge()
	{
		return new HarvestBridge(db.harvestDao(), getCropBridge());
	}

	public SeasonBridge getSeasonBridge()
	{
		return new SeasonBridge(db.seasonDao(), getCropBridge(), getHarvestBridge());
	}
}

