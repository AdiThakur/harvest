package data.bridges;

import java.util.List;

import data.daos.HarvestDao;
import data.models.Harvest;

public class HarvestBridge implements IBridge<Harvest>
{
	private final HarvestDao harvestDao;
	private final CropBridge cropBridge;

	HarvestBridge(HarvestDao harvestDao, CropBridge cropBridge)
	{
		this.harvestDao = harvestDao;
		this.cropBridge = cropBridge;
	}

	@Override
	public Harvest insert(Harvest harvest)
	{
		harvest.cropId = harvest.crop.uid;
		harvest.uid = harvestDao.insert(harvest);
		return harvest;
	}

	@Override
	public Harvest getById(long harvestId)
	{
		Harvest harvest = harvestDao.getById(harvestId);
		harvest.crop = cropBridge.getById(harvest.cropId);
		return harvest;
	}

	@Override
	public List<Harvest> getAll()
	{
		List<Harvest> harvests = harvestDao.getAll();
		harvests.forEach(harvest -> {
			harvest.crop = cropBridge.getById(harvest.cropId);
		});

		return harvests;
	}

	@Override
	public int delete(Harvest harvest)
	{
		return harvestDao.delete(harvest);
	}

	public List<Harvest> getAllBySeason(long seasonId)
	{
		List<Harvest> harvests = harvestDao.getAllBySeason(seasonId);
		harvests.forEach(harvest -> {
			harvest.crop = cropBridge.getById(harvest.cropId);
		});

		return harvests;
	}
}
