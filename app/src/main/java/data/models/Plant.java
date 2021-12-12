package data.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "plant")
public class Plant extends BaseEntity
{
	@Ignore
	public static int NAME_CHARACTER_LIMIT = 50;

	@ColumnInfo(name = "name")
	public String name;

	@ColumnInfo(name = "unit_weight")
	public double unitWeight;

	@ColumnInfo(name = "image_filename")
	public String imageFileName;

	public Plant() {}

	@Ignore
	public Plant(String name, double unitWeight, String imageFileName)
	{
		this.name = name;
		this.unitWeight = unitWeight;
		this.imageFileName = imageFileName;
	}
}
