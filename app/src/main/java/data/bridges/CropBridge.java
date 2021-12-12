package data.bridges;

import java.util.List;

import data.daos.CropDao;
import data.models.Crop;

public class CropBridge
{
	private final CropDao cropDao;
	private final PlantBridge plantBridge;

	CropBridge(CropDao cropDao, PlantBridge plantBridge)
	{
		this.cropDao = cropDao;
		this.plantBridge = plantBridge;
	}

	public Crop insert(Crop crop)
	{
		crop.plantId = crop.plant.uid;
		crop.uid = cropDao.insert(crop);
		return crop;
	}

	public int update(Crop crop)
	{
		if (crop.uid == 0) { return 0; }
		return cropDao.update(crop);
	}

	public Crop getById(long cropId)
	{
		Crop crop = cropDao.get(cropId);
		crop.plant = plantBridge.getById(crop.plantId);
		return crop;
	}

	public List<Crop> getAll()
	{
		List<Crop> crops = cropDao.getAll();
		crops.forEach(crop -> {
			crop.plant = plantBridge.getById(crop.plantId);
		});

		return crops;
	}

	public int delete(Crop crop)
	{
		return cropDao.delete(crop.seasonId, crop.uid);
	}

	public List<Crop> getAllBySeason(long seasonId)
	{
		List<Crop> crops = cropDao.getAllBySeason(seasonId);
		crops.forEach(crop -> {
			crop.plant = plantBridge.getById(crop.plantId);
		});

		return crops;
	}
}
