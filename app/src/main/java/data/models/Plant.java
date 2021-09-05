package data.models;

import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "plant")
public class Plant
{
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
}
