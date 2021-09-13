package data.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "crop")
public class Crop
{
	// FK into season table
	@ColumnInfo(name = "season_id")
	public long seasonId;

	// FK into plant table
	@ColumnInfo(name = "plant_id")
	public long plantId;

	@PrimaryKey(autoGenerate = true)
	public long uid;

	@ColumnInfo(name = "date_planted")
	public LocalDate datePlanted;

	@ColumnInfo(name = "number_of_plants")
	public int numberOfPlants;

	@Ignore
	public Plant plant;

	public Crop() {}

	public Crop(long seasonId, LocalDate datePlanted, int numberOfPlants, Plant plant)
	{
		this.seasonId = seasonId;
		this.datePlanted = datePlanted;
		this.numberOfPlants = numberOfPlants;
		this.plant = plant;
	}
}
