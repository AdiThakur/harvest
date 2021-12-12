package data.bridges;

import java.util.List;

import data.daos.PlantDao;
import data.models.Plant;

public class PlantBridge
{
	private final PlantDao plantDao;

	PlantBridge(PlantDao plantDao)
	{
		this.plantDao = plantDao;
	}

	public Plant insert(Plant plant)
	{
		plant.uid = plantDao.insert(plant);
		return plant;
	}

	public int update(Plant plant)
	{
		if (plant.uid == 0) { return 0; }
		return plantDao.update(plant);
	}

	public Plant getById(long plantId)
	{
		return plantDao.get(plantId);
	}

	public List<Plant> getAll()
	{
		return plantDao.getAll();
	}

	public int delete(Plant plant)
	{
		return plantDao.delete(plant.uid);
	}

	public Plant get(String plantName)
	{
		return plantDao.getByName(plantName);
	}
}
