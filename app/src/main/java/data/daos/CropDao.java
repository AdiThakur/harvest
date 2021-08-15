package data.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;

import data.models.Crop;

@Dao
public interface CropDao
{
	@Insert
	public long insert(Crop Crop);

	@Query("SELECT * FROM crop WHERE uid = :cropId")
	public Crop getById(long cropId);

	@Query("SELECT * FROM crop WHERE season_id = :seasonId")
	public List<Crop> getAllBySeason(long seasonId);

	@Query("SELECT * FROM crop")
	public List<Crop> getAll();

	@Delete
	public int delete(Crop Crop);
}
