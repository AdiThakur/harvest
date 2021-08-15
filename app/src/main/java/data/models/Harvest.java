package data.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.util.Date;

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
	public LocalDateTime dateHarvested;

	@ColumnInfo(name = "count")
	public int count;

	@Ignore
	public Crop crop;
}
