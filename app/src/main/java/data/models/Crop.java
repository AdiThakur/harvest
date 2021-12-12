package data.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity(tableName = "crop")
public class Crop extends BaseEntity
{
	// FK into season table
	@ColumnInfo(name = "season_id")
	public long seasonId;

	// FK into plant table
	@ColumnInfo(name = "plant_id")
	public long plantId;

	@ColumnInfo(name = "date_planted")
	public LocalDate datePlanted;

	@ColumnInfo(name = "number_of_plants")
	public int numberOfPlants;

	@Ignore
	public Plant plant;

	public Crop() {}

	@Ignore
	public Crop(long seasonId, LocalDate datePlanted, int numberOfPlants, Plant plant)
	{
		this.seasonId = seasonId;
		this.datePlanted = datePlanted;
		this.numberOfPlants = numberOfPlants;
		this.plant = plant;
	}

	@NonNull
	@Override
	public String toString() {
		return plant.name + " (" + seasonId + ")";
	}

	public static Crop ShallowCopy(Crop toCopy)
	{
		Crop copy = new Crop();
		copy.uid = toCopy.uid;
		copy.seasonId = toCopy.seasonId;
		copy.plantId = toCopy.plantId;
		copy.numberOfPlants = toCopy.numberOfPlants;
		copy.datePlanted = toCopy.datePlanted;
		copy.plant = toCopy.plant;

		return copy;
	}

	public static List<String> distinctNames(List<Crop> crops)
	{
		HashMap<String, List<Crop>> namesMap = new HashMap<>();
		List<String> distinctNames = new ArrayList<>();

		for (Crop crop : crops) {
			if (!namesMap.containsKey(crop.plant.name)) {
				namesMap.put(crop.plant.name, new ArrayList<>());
			}
			namesMap.get(crop.plant.name).add(crop);
		}

		for (String key : namesMap.keySet()) {
			List<Crop> sameNames = namesMap.get(key);
			if (sameNames.size() == 1) {
				distinctNames.add(sameNames.get(0).plant.name);
			} else {
				for (Crop crop : sameNames) {
					distinctNames.add(String.format("%s (%d)", crop.plant.name, crop.seasonId));
				}
			}
		}

		return distinctNames;
	}
}
