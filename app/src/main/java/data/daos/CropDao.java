package data.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import data.models.Crop;

@Dao
public interface CropDao
{
	@Insert
	public long insert(Crop crop);

	@Update
	public int update(Crop crop);

	@Query("SELECT * FROM crop WHERE uid = :cropId")
	public Crop getById(long cropId);

	@Query("SELECT * FROM crop WHERE season_id = :seasonId")
	public List<Crop> getAllBySeason(long seasonId);

	@Query("SELECT * FROM crop")
	public List<Crop> getAll();

	@Query("DELETE FROM crop WHERE season_id = :seasonId AND uid = :cropId AND NOT EXISTS (SELECT * FROM harvest WHERE harvest.crop_id = :cropId)")
	public int delete(long seasonId, long cropId);
}
