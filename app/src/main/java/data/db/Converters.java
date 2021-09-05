package data.db;

import android.net.Uri;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import common.Helper;

public class Converters
{
	@TypeConverter
	public static LocalDateTime dateFromLong(Long value)
	{
		if (value == null) {
			return null;
		}

		return Helper.dateFromSeconds(value);
	}

	@TypeConverter
	public static Long dateToLong(LocalDateTime date)
	{
		return date == null ? null : date.atZone(ZoneOffset.UTC).toEpochSecond();
	}

	@TypeConverter
	public static Uri uriFromString(String value)
	{
		return Uri.parse(value);
	}

	@TypeConverter
	public static String uriToString(Uri uri)
	{
		if (uri == null) {
			return null;
		}

		return uri.toString();
	}
}
