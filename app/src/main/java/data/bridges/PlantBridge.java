package data.bridges;

import androidx.annotation.NonNull;

import data.daos.PlantDao;
import data.models.Plant;

public class PlantBridge extends BaseBridge<Plant>
{
	private final PlantDao plantDao;

	PlantBridge(PlantDao plantDao)
	{
		super(plantDao);
		this.plantDao = plantDao;
	}

	public Plant get(String plantName)
	{
		return plantDao.getByName(plantName);
	}

	@Override
	public int delete(Plant plant) {
		return plantDao.delete(plant.uid);
	}
}
