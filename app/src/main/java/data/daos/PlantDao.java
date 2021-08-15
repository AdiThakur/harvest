package data.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;

import data.models.Plant;

@Dao
public interface PlantDao
{
	@Insert
	public long insert(Plant plant);

	@Query("SELECT * FROM plant WHERE uid = :plantId")
	public Plant getById(long plantId);

	@Query("SELECT * FROM plant WHERE name = :plantName")
	public Plant getByName(String plantName);

	@Query("SELECT * FROM plant")
	public List<Plant> getAll();

	@Delete
	public int delete(Plant plant);
}
