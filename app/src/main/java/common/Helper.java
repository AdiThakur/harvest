package common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Helper
{
	public static final String defaultPlantImageUriString =
			"android.resource://com.example.harvest/drawable/tomato";

	public static String shortFormatOfDate(LocalDateTime date)
	{
		return date.format(DateTimeFormatter.ofPattern("d MMM yyyy"));
	}
}
