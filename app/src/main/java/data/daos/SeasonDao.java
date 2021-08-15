package data.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;

import data.models.Season;

@Dao
public interface SeasonDao
{
	@Insert
	public long insert(Season season);

	@Query("SELECT * FROM season WHERE year = :year")
	public Season getById(long year);

	@Query("SELECT * FROM season")
	public List<Season> getAll();

	@Query("SELECT * FROM season ORDER BY year DESC LIMIT 1")
	public Season getLatestSeason();

	@Delete
	public int delete(Season season);
}
