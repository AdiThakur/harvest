package data.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import data.models.Plant;

@Dao
public interface PlantDao extends IDao<Plant>
{
	@Override
	@Insert
	long insert(Plant entity);

	@Override
	@Query("SELECT * FROM plant WHERE uid = :uid")
	Plant get(long uid);

	@Override
	@Query("SELECT * FROM plant")
	List<Plant> getAll();

	@Override
	@Update
	int update(Plant entity);

	@Override
	@Delete
	int delete(Plant entity);

	// Plant specific methods

	@Query("DELETE FROM plant WHERE uid = :uid AND NOT EXISTS (SELECT * FROM crop WHERE crop.plant_id = :uid)")
	int delete(long uid);

	@Query("SELECT * FROM plant WHERE name = :plantName")
	Plant getByName(String plantName);
}
