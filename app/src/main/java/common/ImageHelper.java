package common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.harvest.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class ImageHelper
{
	/**
	 * Converts an image to a Bitmap.
	 *
	 * @param context			App context to access internal storage APIs
	 * @param imageUri			Uri of image to convert
	 * @return 					Bitmap of image @imageFileName on success, Bitmap of default
	 * 							Plant image otherwise.
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

	public static String extractFileNameFromUri(Uri uri)
	{
		List<String> segments = uri.getPathSegments();
		return segments.get(segments.size() - 1);
	}

	public static String saveImage(Context context, Uri uri)
	{
		Bitmap imageBitmap = convertImageToBitmap(context, uri);
		return saveBitmapToImage(context, imageBitmap, extractFileNameFromUri(uri));
	}

	public static boolean deleteImage(Context context, String fileName)
	{
		return context.deleteFile(fileName);
	}
}
