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
		version = 2,
		exportSchema = false
)
@TypeConverters({ Converters.class })
public abstract class HarvestDB extends RoomDatabase
{
	public abstract CropDao cropDao();
	public abstract HarvestDao harvestDao();
	public abstract PlantDao plantDao();
	public abstract SeasonDao seasonDao();

	static final Migration MIGRATION_1_2 = new Migration(1, 2)
	{
		@Override
		public void migrate(@NonNull SupportSQLiteDatabase database)
		{
			database.execSQL("" +
				"CREATE TABLE plant_backup (" +
					"uid INTEGER, " +
					"name TEXT, " +
					"unit_weight DECIMAL, " +
					"image_uri TEXT DEFAULT ('" + Helper.defaultPlantImageUriString + "')," +
					"PRIMARY KEY (uid)" +
				")"
			);
			database.execSQL("INSERT INTO plant_backup(uid, name, unit_weight) SELECT uid, name, unit_weight FROM plant");
			database.execSQL("DROP TABLE plant");
			database.execSQL("ALTER TABLE plant_backup RENAME TO plant");
		}
	};

	private static final String dbName = "harvest_db";
	private static volatile HarvestDB instance;

	public static synchronized HarvestDB getInstance(Context appContext)
	{
		if (instance == null) {
			instance = Room.databaseBuilder(appContext, HarvestDB.class, dbName)
				.allowMainThreadQueries()
				.build();
		}
		return instance;
	}
}
