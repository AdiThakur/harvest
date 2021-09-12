package data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import common.Helper;
import data.daos.CropDao;
import data.daos.HarvestDao;
import data.daos.PlantDao;
import data.daos.SeasonDao;
import data.models.Crop;
import data.models.Harvest;
import data.models.Plant;
import data.models.Season;

@Database(
		entities = { Crop.class, Harvest.class, Plant.class, Season.class },
		version = 6,
		exportSchema = false
)
@TypeConverters({ Converters.class })
public abstract class HarvestDB extends RoomDatabase
{
	public abstract CropDao cropDao();
	public abstract HarvestDao harvestDao();
	public abstract PlantDao plantDao();
	public abstract SeasonDao seasonDao();

	private static final String dbName = "harvest_db";
	private static volatile HarvestDB instance;

	public static synchronized HarvestDB getInstance(Context appContext)
	{
		if (instance == null) {
			instance = Room.databaseBuilder(appContext, HarvestDB.class, dbName)
				.allowMainThreadQueries()
				.fallbackToDestructiveMigration()
				.build();
		}
		return instance;
	}
}
