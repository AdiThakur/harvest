package data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class BaseEntity
{
	@PrimaryKey(autoGenerate = true)
	public long uid;
}
