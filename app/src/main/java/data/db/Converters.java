package data.db;

import android.net.Uri;

import androidx.room.TypeConverter;

import java.time.LocalDate;

public class Converters
{
	@TypeConverter
	public static LocalDate dateFromLong(String value)
	{
		if (value == null) { return null; }
		return LocalDate.parse(value);
	}

	@TypeConverter
	public static String dateToLong(LocalDate date)
	{
		if (date == null) { return null; }
		return date.toString();
	}
}
