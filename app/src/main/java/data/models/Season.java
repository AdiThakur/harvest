package data.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.util.List;

@Entity(tableName = "season")
public class Season extends BaseEntity
{
	@ColumnInfo(name = "year")
	public long year;

	@Ignore
	public List<Crop> crops;

	@Ignore
	public List<Harvest> harvests;

	public Season() {}

	@Ignore
	public Season(long year)
	{
		this.year = year;
	}
}
