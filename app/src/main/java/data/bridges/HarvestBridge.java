package data.bridges;

import java.util.List;

import data.daos.HarvestDao;
import data.models.Harvest;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.SingleSubject;

public class HarvestBridge extends BaseBridge<Harvest>
{
	private final HarvestDao harvestDao;
	private final CropBridge cropBridge;

	HarvestBridge(HarvestDao harvestDao, CropBridge cropBridge)
	{
		super(harvestDao);
		this.harvestDao = harvestDao;
		this.cropBridge = cropBridge;
	}

	@Override
	public Harvest insert(Harvest harvest)
	{
		harvest.cropId = harvest.crop.uid;
		return super.insert(harvest);
	}

	@Override
	public Harvest get(long harvestId)
	{
		Harvest harvest = super.get(harvestId);
		harvest.crop = cropBridge.get(harvest.cropId);
		return harvest;
	}

	@Override
	public List<Harvest> getAll()
	{
		List<Harvest> harvests = super.getAll();
		harvests.forEach(harvest -> {
			harvest.crop = cropBridge.get(harvest.cropId);
		});

		return harvests;
	}

	public Single<List<Harvest>> getAllBySeason(long seasonId)
	{
		SingleSubject<List<Harvest>> harvestsSubject = SingleSubject.create();

		harvestDao
			.getAllBySeason(seasonId)
			.subscribeOn(Schedulers.io())
			.observeOn(Schedulers.io())
			.map(harvests -> {
				harvests.forEach(harvest -> harvest.crop = cropBridge.get(harvest.cropId));
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
				harvests.forEach(harvest -> harvest.crop = cropBridge.get(harvest.cropId));
				return harvests;
			})
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(harvests -> {
				harvestsSubject.onSuccess(harvests);
			});

		return harvestsSubject.hide();
	}
}
