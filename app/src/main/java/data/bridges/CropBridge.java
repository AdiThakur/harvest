package data.bridges;

import java.util.List;

import data.daos.CropDao;
import data.models.Crop;

public class CropBridge extends BaseBridge<Crop>
{
	private final CropDao cropDao;
	private final PlantBridge plantBridge;

	CropBridge(CropDao cropDao, PlantBridge plantBridge)
	{
		super(cropDao);
		this.cropDao = cropDao;
		this.plantBridge = plantBridge;
	}

	public Crop insert(Crop crop)
	{
		crop.plantId = crop.plant.uid;
		return super.insert(crop);
	}

	public Crop get(long cropId)
	{
		Crop crop = super.get(cropId);
		crop.plant = plantBridge.get(crop.plantId);
		return crop;
	}

	public List<Crop> getAll()
	{
		List<Crop> crops = super.getAll();
		crops.forEach(crop -> {
			crop.plant = plantBridge.get(crop.plantId);
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
			crop.plant = plantBridge.get(crop.plantId);
		});

		return crops;
	}
}
