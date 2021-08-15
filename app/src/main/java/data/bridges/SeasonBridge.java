package data.bridges;

import java.util.ArrayList;
import java.util.List;

import data.daos.SeasonDao;
import data.models.Season;

public class SeasonBridge implements IBridge<Season>
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

	@Override
	public Season insert(Season season)
	{
		seasonDao.insert(season);
		return season;
	}

	@Override
	public Season getById(long year)
	{
		Season season = seasonDao.getById(year);
		season.crops = cropBridge.getAllBySeason(year);
		season.harvests = harvestBridge.getAllBySeason(year);

		return season;
	}

	@Override
	public List<Season> getAll()
	{
		List<Long> years = getAllYears();
		List<Season> seasons = new ArrayList<>();

		years.forEach(year -> {
			seasons.add(getById(year));
		});

		return seasons;
	}

	@Override
	public int delete(Season model)
	{
		return seasonDao.delete(model);
	}

	public Season getLatestSeason()
	{
		return seasonDao.getLatestSeason();
	}

	public List<Long> getAllYears()
	{
		List<Long> years = new ArrayList<>();
		seasonDao.getAll().forEach(season -> {
			years.add(season.year);
		});

		return years;
	}
}
