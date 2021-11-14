package data.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "plant")
public class Plant
{
	@Ignore
	public static int NAME_CHARACTER_LIMIT = 50;

	@PrimaryKey(autoGenerate = true)
	public long uid;

	@ColumnInfo(name = "name")
	public String name;

	@ColumnInfo(name = "unit_weight")
	public double unitWeight;

	@ColumnInfo(name = "image_filename")
	public String imageFileName;

	public Plant() {}

	public Plant(String name, double unitWeight, String imageFileName)
	{
		this.name = name;
		this.unitWeight = unitWeight;
		this.imageFileName = imageFileName;
	}

	public static Plant ShallowCopy(Plant toCopy)
	{
		Plant copy = new Plant();
		copy.uid = toCopy.uid;
		copy.name = toCopy.name;
		copy.unitWeight = toCopy.unitWeight;
		copy.imageFileName = toCopy.imageFileName;

		return copy;
	}
}
