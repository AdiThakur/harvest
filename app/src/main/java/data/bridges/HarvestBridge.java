package data.bridges;

import java.util.List;

import data.daos.HarvestDao;
import data.models.Harvest;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.SingleSubject;

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

	public int update(Harvest harvest)
	{
		if (harvest.uid == 0) { return 0; }
		return harvestDao.update(harvest);
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

	public Single<List<Harvest>> getAllBySeason(long seasonId)
	{
		SingleSubject<List<Harvest>> harvestsSubject = SingleSubject.create();

		harvestDao
			.getAllBySeason(seasonId)
			.subscribeOn(Schedulers.io())
			.observeOn(Schedulers.io())
			.map(harvests -> {
				harvests.forEach(harvest -> harvest.crop = cropBridge.getById(harvest.cropId));
				return harvests;
			})
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(harvests -> {
				harvestsSubject.onSuccess(harvests);
			});

		return harvestsSubject.hide();
	}

	public Single<List<Harvest>> getAllBySeasonAndPlantIds(List<Long> seasonIds, List<Long> cropIds)
	{
		SingleSubject<List<Harvest>> harvestsSubject = SingleSubject.create();

		harvestDao
			.getAllBySeasonAndCropIds(seasonIds, cropIds)
			.subscribeOn(Schedulers.io())
			.observeOn(Schedulers.io())
			.map(harvests -> {
				harvests.forEach(harvest -> harvest.crop = cropBridge.getById(harvest.cropId));
				return harvests;
			})
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(harvests -> {
				harvestsSubject.onSuccess(harvests);
			});

		return harvestsSubject.hide();
	}
}
