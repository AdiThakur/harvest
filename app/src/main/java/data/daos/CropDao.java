package data.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import data.models.Crop;

@Dao
public interface CropDao extends IDao<Crop>
{
	@Override
	@Insert
	long insert(Crop entity);

	@Override
	@Query("SELECT * FROM crop WHERE uid = :uid")
	Crop get(long uid);

	@Override
	@Query("SELECT * FROM crop")
	List<Crop> getAll();

	@Override
	@Update
	int update(Crop entity);

	@Override
	@Delete
	int delete(Crop entity);

	// Crop specific methods

	// TODO: Figure out why the hell we need seasonId here
	@Query("DELETE FROM crop WHERE season_id = :seasonId AND uid = :cropUid AND NOT EXISTS (SELECT * FROM harvest WHERE harvest.crop_id = :cropUid)")
	int delete(long seasonId, long cropUid);

	@Query("SELECT * FROM crop WHERE season_id = :seasonId")
	List<Crop> getAllBySeason(long seasonId);
}
