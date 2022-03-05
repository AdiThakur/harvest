package common;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class Helper
{
	public static final String defaultPlantImageUriString =
			"android.resource://com.example.harvest/drawable/tomato";

	/**
	 *	Formats a Date object into a short string.
	 *
	 * @param date			Date object to convert into a short and sweet String.
	 * @return				String representing the specified date; formatted like 21 Aug, 2021
	 */
	public static String shortFormatOfDate(LocalDate date)
	{
		return date.format(DateTimeFormatter.ofPattern("d MMM yyyy"));
	}

	public static String dateToISO8601(LocalDate date)
	{
		return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

	/**
	 * Compares the two dates.
	 *
	 * @param d1			Date 1
	 * @param d2			Date 2
	 * @return				-1 if d1 occurs BEFORE d2
	 * 						 0 if d1 and d2 refer to the same date
	 * 						+1 if d1 occurs AFTER d2
	 */
	public static int compareDates(LocalDate d1, LocalDate d2)
	{
		if (d1.isBefore(d2)) {
			return -1;
		} else if (d1.isEqual(d2)) {
			return 0;
		} else {
			return 1;
		}
	}

	public static String inGrams(double weight)
	{
		return weight + " (g)";
	}

	public static String formatUnitWeight(double weight, boolean includeGrams)
	{
		if (weight > 0) {
			DecimalFormat df = new DecimalFormat("#.00");
			String formatted = df.format(weight);
			if (includeGrams) {
				return formatted + " (g)";
			} else {
				return formatted;
			}
		}

		return "No data";
	}

	public static <N extends Number> String formatData(N data)
	{
		if (data.doubleValue() > 0) {
			return String.valueOf(data);
		}

		return "No data";
	}

	public static <T> List<String> allToString(List<T> items)
	{
		return items.stream().map(Object::toString).collect(Collectors.toList());
	}
}
