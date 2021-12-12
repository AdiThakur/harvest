package data.bridges;

import java.util.List;
import java.util.stream.Collectors;

import data.daos.SeasonDao;
import data.models.Season;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.SingleSubject;

public class SeasonBridge extends BaseBridge<Season>
{
	private final SeasonDao seasonDao;
	private final CropBridge cropBridge;
	private final HarvestBridge harvestBridge;

	SeasonBridge(SeasonDao seasonDao, CropBridge cropBridge, HarvestBridge harvestBridge)
	{
		super(seasonDao);
		this.seasonDao = seasonDao;
		this.cropBridge = cropBridge;
		this.harvestBridge = harvestBridge;
	}

	public Season getLatestSeason()
	{
		return seasonDao.getLatestSeason();
	}

	public Single<List<Long>> getAllYears()
	{
		SingleSubject<List<Long>> subject = SingleSubject.create();

		seasonDao
			.getAsync()
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
