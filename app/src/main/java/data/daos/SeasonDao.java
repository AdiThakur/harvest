package data.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import data.models.Season;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface SeasonDao extends IDao<Season>
{
	@Override
	@Insert
	long insert(Season season);

	@Override
	@Query("SELECT * FROM season WHERE year = :year")
	Season get(long year);

	@Override
	@Query("SELECT * FROM season")
	List<Season> getAll();

	@Override
	@Update
	int update(Season season);

	@Override
	@Delete
	int delete(Season season);

	// Season specific methods

	@Query("SELECT * FROM season")
	Single<List<Season>> getAsync();

	@Query("SELECT * FROM season ORDER BY year DESC LIMIT 1")
	Season getLatestSeason();
}
