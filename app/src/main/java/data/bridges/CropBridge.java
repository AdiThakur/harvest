package data.bridges;

import java.util.List;

import data.daos.CropDao;
import data.models.Crop;

public class CropBridge implements IBridge<Crop>
{
	private final CropDao cropDao;
	private final PlantBridge plantBridge;

	CropBridge(CropDao cropDao, PlantBridge plantBridge)
	{
		this.cropDao = cropDao;
		this.plantBridge = plantBridge;
	}

	@Override
	public Crop insert(Crop crop)
	{
		crop.plantId = crop.plant.uid;
		crop.uid = cropDao.insert(crop);
		return crop;
	}

	@Override
	public Crop getById(long cropId)
	{
		Crop crop = cropDao.getById(cropId);
		crop.plant = plantBridge.getById(crop.plantId);
		return crop;
	}

	@Override
	public List<Crop> getAll()
	{
		List<Crop> crops = cropDao.getAll();
		crops.forEach(crop -> {
			crop.plant = plantBridge.getById(crop.plantId);
		});

		return crops;
	}

	@Override
	public int delete(Crop crop)
	{
		return cropDao.delete(crop);
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
