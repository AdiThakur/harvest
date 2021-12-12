package data.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import data.models.Harvest;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface HarvestDao
{
	@Insert
	long insert(Harvest harvest);

	@Update
	int update(Harvest harvest);

	@Query("SELECT * FROM harvest WHERE uid = :harvestId")
	Harvest getById(long harvestId);

	@Query("SELECT * FROM harvest")
	List<Harvest> getAll();

	@Query("SELECT * FROM harvest WHERE season_id = :seasonId")
	Single<List<Harvest>> getAllBySeason(long seasonId);

	@Query("SELECT * FROM harvest WHERE season_id IN (:seasonIds) AND crop_id IN (:cropIds)")
	Single<List<Harvest>> getAllBySeasonAndCropIds(List<Long> seasonIds, List<Long> cropIds);

	@Delete
	int delete(Harvest harvest);
}
