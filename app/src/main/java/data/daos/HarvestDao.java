package data.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import data.models.Harvest;

@Dao
public interface HarvestDao
{
	@Insert
	public long insert(Harvest harvest);

	@Update
	public int update(Harvest harvest);

	@Query("SELECT * FROM harvest WHERE uid = :harvestId")
	public Harvest getById(long harvestId);

	@Query("SELECT * FROM harvest")
	public List<Harvest> getAll();

	@Query("SELECT * FROM harvest WHERE season_id = :seasonId")
	public List<Harvest> getAllBySeason(long seasonId);

	@Query("SELECT * FROM harvest WHERE season_id IN (:seasonIds) AND crop_id IN (:cropIds)")
	public List<Harvest> getAllBySeasonAndCropIds(List<Long> seasonIds, List<Long> cropIds);

	@Delete
	public int delete(Harvest harvest);
}
