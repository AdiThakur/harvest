package data.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;

import data.models.Harvest;

@Dao
public interface HarvestDao
{
	@Insert
	public long insert(Harvest harvest);

	@Query("SELECT * FROM harvest WHERE uid = :harvestId")
	public Harvest getById(long harvestId);

	@Query("SELECT * FROM harvest")
	public List<Harvest> getAll();

	@Query("SELECT * FROM harvest WHERE season_id = :seasonId")
	public List<Harvest> getAllBySeason(long seasonId);

	@Delete
	public int delete(Harvest harvest);
}
