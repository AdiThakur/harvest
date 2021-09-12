package data.db;

import android.net.Uri;
import androidx.room.TypeConverter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import common.Helper;

public class Converters
{
	@TypeConverter
	public static LocalDateTime dateFromLong(Long value)
	{
		return (value == null) ? null : Helper.dateFromSeconds(value);
	}

	@TypeConverter
	public static Long dateToLong(LocalDateTime date)
	{
		if (date == null) { return null; }

		date.minusHours(date.getHour());
		date.minusMinutes(date.getMinute());
		date.minusSeconds(date.getMinute());
		date.minusNanos(date.getNano());
		return date.atZone(ZoneOffset.UTC).toEpochSecond();
	}

	@TypeConverter
	public static Uri uriFromString(String value)
	{
		return Uri.parse(value);
	}

	@TypeConverter
	public static String uriToString(Uri uri)
	{
		return (uri == null) ? null : uri.toString();
	}
}
