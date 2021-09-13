package common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.harvest.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

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
	public static String shortFormatOfDate(LocalDateTime date)
	{
		return date.format(DateTimeFormatter.ofPattern("d MMM yyyy"));
	}

	public static LocalDateTime dateFromSeconds(long seconds)
	{
		 return LocalDateTime.ofEpochSecond(
			seconds, 0, OffsetDateTime.now().getOffset()
	 	);
	}

	/**
	 * Compares the two dates while ignoring the associated times.
	 *
	 * @param d1			Date 1
	 * @param d2			Date 2
	 * @return				-1 if d1 occurs BEFORE d2
	 * 						 0 if d1 and d2 refer to the same date
	 * 						+1 if d1 occurs AFTER d2
	 */
	public static int compareDates(LocalDateTime d1, LocalDateTime d2)
	{
		int day1 = d1.getDayOfMonth();
		int month1 = d1.getMonthValue();
		int year1 = d1.getYear();

		int day2 = d2.getDayOfMonth();
		int month2 = d2.getMonthValue();
		int year2 = d2.getYear();

		if ((day1 == day2) && (month1 == month2) && (year1 == year2)) {
			return 0;
		} else if (d1.isBefore(d2)) {
			return -1;
		} else {
			return 1;
		}
	}

	/**
	 * Converts an image to a Bitmap.
	 *
	 * @param context			App context to access internal storage APIs
	 * @param imageUri			Uri of image to convert
	 * @return 					Bitmap of image @imageFileName on success, Bitmap of default
 * 	 * 							Plant image otherwise.
	 */
	public static Bitmap convertImageToBitmap(Context context, Uri imageUri)
	{
		Bitmap plantBitmap;

		try {
			plantBitmap =
					MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
		} catch (IOException e) {
			plantBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.tomato);
		}

		return plantBitmap;
	}

	/**
	 * Saves a Bitmap as a PNG image in the app specific Internal storage.
	 *
	 * @param context			App context to access internal storage APIs
	 * @param imageBitmap		Bitmap to save
	 * @param imageName			Name of image; used to construct an unique filename
	 * @return					Filename of image on success, null otherwise
	 */
	public static String saveBitmapToImage(Context context, Bitmap imageBitmap, String imageName)
	{
		// Need unique filename
		String fileName = Calendar.getInstance().getTime().toString() + " " + imageName + ".png";
		fileName = fileName.replaceAll(" ", "_");

		try {
			FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
			return fileName;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Loads a Bitmap from the PNG image with name @imageFileName
	 *
	 * @param context			App context to access internal storage APIs
	 * @param imageFileName		Name of PNG to load as a Bitmap
	 * @return					Bitmap of image @imageFileName on success, Bitmap of default
	 * 							Plant image otherwise.
	 */
	public static Bitmap loadBitmapFromImage(Context context, String imageFileName)
	{
		try {
			FileInputStream fis = context.openFileInput(imageFileName);
			return BitmapFactory.decodeStream(fis);
		} catch (IOException e) {
			return BitmapFactory.decodeResource(context.getResources(), R.drawable.tomato);
		}
	}
}
