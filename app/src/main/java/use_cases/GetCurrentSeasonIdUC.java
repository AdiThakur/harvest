package use_cases;

import android.content.Context;

import java.time.LocalDate;

import data.bridges.BridgeFactory;
import data.bridges.SeasonBridge;
import data.models.Season;

/**
 * Use Case: Gets the latest Season. If a Season representing the current year doesn't exist in the
 * DB, it is added.
 */
public class GetCurrentSeasonIdUC
{
	private final SeasonBridge bridge;

	public GetCurrentSeasonIdUC(Context context)
	{
		BridgeFactory factory = new BridgeFactory(context);
		bridge = factory.getSeasonBridge();
	}

	public long use()
	{
		Season latest = bridge.getLatestSeason();
		long currYear = LocalDate.now().getYear();

		// Most recent Season in DB represents this year
		if (latest != null && latest.year == currYear) {
			return latest.year;
		}

		Season newSeason = new Season(currYear);
		newSeason = bridge.insert(newSeason);

		return newSeason.year;
	}
}
