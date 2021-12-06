package data.bridges;

import java.util.List;
import java.util.stream.Collectors;

import data.daos.SeasonDao;
import data.models.Season;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.SingleSubject;

public class SeasonBridge
{
	private final SeasonDao seasonDao;
	private final CropBridge cropBridge;
	private final HarvestBridge harvestBridge;

	SeasonBridge(SeasonDao seasonDao, CropBridge cropBridge, HarvestBridge harvestBridge)
	{
		this.seasonDao = seasonDao;
		this.cropBridge = cropBridge;
		this.harvestBridge = harvestBridge;
	}

	public Season insert(Season season)
	{
		seasonDao.insert(season);
		return season;
	}

	public Season getById(long year)
	{
		Season season = seasonDao.getById(year);
		season.crops = cropBridge.getAllBySeason(year);
		season.harvests = harvestBridge.getAllBySeason(year);

		return season;
	}

	public int delete(Season model)
	{
		return seasonDao.delete(model);
	}

	public Season getLatestSeason()
	{
		return seasonDao.getLatestSeason();
	}

	public Single<List<Long>> getAllYears()
	{
		SingleSubject<List<Long>> subject = SingleSubject.create();

		seasonDao
			.getAll()
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(seasons -> {
				List<Long> years =
					seasons.stream().map(season -> season.year).collect(Collectors.toList());
				subject.onSuccess(years);
			});

		return subject.hide();
	}
}
