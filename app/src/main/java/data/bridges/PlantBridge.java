package data.bridges;

import java.util.List;

import data.daos.PlantDao;
import data.models.Plant;

public class PlantBridge implements IBridge<Plant>
{
	private final PlantDao plantDao;

	PlantBridge(PlantDao plantDao)
	{
		this.plantDao = plantDao;
	}

	@Override
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

	@Override
	public Plant getById(long plantId)
	{
		return plantDao.getById(plantId);
	}

	@Override
	public List<Plant> getAll()
	{
		return plantDao.getAll();
	}

	@Override
	public int delete(Plant plant)
	{
		return plantDao.delete(plant.uid);
	}

	public Plant get(String plantName)
	{
		return plantDao.getByName(plantName);
	}
}
