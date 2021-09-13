package data.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "harvest")
public class Harvest
{
	// FK into season table
	@ColumnInfo(name = "season_id")
	public long seasonId;

	// FK into crop table
	@ColumnInfo(name = "crop_id")
	public long cropId;

	@PrimaryKey(autoGenerate = true)
	public long uid;

	@ColumnInfo(name = "date_harvested")
	public LocalDate dateHarvested;

	@ColumnInfo(name = "count")
	public int unitsHarvested;

	@ColumnInfo(name ="weight")
	public double totalWeight;

	@Ignore
	public Crop crop;

	public Harvest() {}

	public Harvest(
			long seasonId, int unitsHarvested, double totalWeight, LocalDate dateHarvested, Crop crop)
	{
		this.seasonId = seasonId;
		this.unitsHarvested = unitsHarvested;
		this.totalWeight = totalWeight;
		this.dateHarvested = dateHarvested;
		this.crop = crop;
	}
}
