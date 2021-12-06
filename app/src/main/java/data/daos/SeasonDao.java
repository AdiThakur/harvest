package data.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import data.models.Season;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface SeasonDao
{
	@Insert
	long insert(Season season);

	@Query("SELECT * FROM season WHERE year = :year")
	Season getById(long year);

	@Query("SELECT * FROM season")
	Single<List<Season>> getAll();

	@Query("SELECT * FROM season ORDER BY year DESC LIMIT 1")
	Season getLatestSeason();

	@Delete
	int delete(Season season);
}
