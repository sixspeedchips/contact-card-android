/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.service;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.libsoft.contactcard.R;
import io.libsoft.contactcard.model.entity.Image;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


/**
 * File manager service class, all file and path requests are handled here
 */
public class FileManagerService {

  private static final String TESS_DATA_DIRECTORY = "/tessdata/";
  private static final String IMAGE_DIRECTORY = "/images/";
  private static final String NAMES_DIRECTORY = "/names/";
  private static Application applicationContext;
  private static String TAG = "FileManagerService";
  private static String APP_PATH;
  private MutableLiveData<File> fileData;

  private FileManagerService() {
    fileData = new MutableLiveData<>();
  }

  /**
   * Sets the context required by any services used by this service.
   *
   * @param applicationContext {@link android.content.Context} used for signing in.
   */
  public static void setApplicationContext(Application applicationContext) {
    FileManagerService.applicationContext = applicationContext;
    APP_PATH = applicationContext.getExternalFilesDir(null).getPath();
  }

  /**
   * Get the singleton instance for the service
   *
   * @return FileManagerService instance
   */
  public static FileManagerService getInstance() {
    return FileManagerService.InstanceHolder.INSTANCE;
  }

  public LiveData<File> getFileData() {
    return fileData;
  }

  /**
   * Save method which converts {@link Bitmap} into a a file, with the name at the given time.
   * @param bitmap to be saved
   * @return {@link File} path the bitmap was saved to.
   */
  public File saveImage(Bitmap bitmap) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bitmap.compress(CompressFormat.PNG, 100, stream);
    byte[] bytes = stream.toByteArray();
    bitmap.recycle();
    return saveImage(bytes);
  }

  /**
   * Save method which converts {@link byte} array into a a file, with the name at the given time.
   * @param bytes to be saved into an image.
   * @return {@link File} path the bytes were saved to.
   */
  public File saveImage(byte[] bytes) {

    File file = getImagePath();
    try (OutputStream output = new FileOutputStream(file)) {
      output.write(bytes);
      Image image = new Image();
      image.setRaw(false);
      image.setUrl(file.getAbsolutePath());
      ContactDatabase.getInstance().getImageDao().insert(image);
      fileData.postValue(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return file;
  }

  /**
   * Loader method called at database initialization to load raw data in the database for future use (Pre-load).
   * @return map containing the List of strings for roughly 100,000 first and last names.
   */
  public Map<String, List<String>> getNames() {
    Log.d(TAG, "getNames: starting");
    Map<String, List<String>> map = new HashMap<>();
    File dir = new File(APP_PATH + NAMES_DIRECTORY);
    if (!dir.exists()) {
      boolean created = dir.mkdir();
      if (created) {
        Log.d(TAG, "Created Directory: " + dir.getPath());
      } else {
        Log.e(TAG, "Failed to create names folder");
      }
    }

    File file = new File(APP_PATH + NAMES_DIRECTORY + "names_first.txt");

    if (!file.exists()) {
      try {
        InputStream is = applicationContext.getResources().openRawResource(R.raw.names_first);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        OutputStream outputStream = new FileOutputStream(file);
        outputStream.write(buffer);
        Log.d(TAG, "names written to file: " + file.getAbsolutePath());
      } catch (IOException e) {
        Log.e(TAG,
            "Error writing names to file: " + file.getAbsolutePath() + " " + e.getMessage());
      }
    }
    map.put("first", parseFile(file));
    file = new File(APP_PATH + NAMES_DIRECTORY + "names_last.txt");

    if (!file.exists()) {
      try {
        InputStream is = applicationContext.getResources().openRawResource(R.raw.names_last);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        OutputStream outputStream = new FileOutputStream(file);
        outputStream.write(buffer);
        Log.d(TAG, "last names written to file: " + file.getAbsolutePath());
      } catch (IOException e) {
        Log.e(TAG,
            "Error writing names to file: " + file.getAbsolutePath() + " " + e.getMessage());
      }
    }
    map.put("last", parseFile(file));

    return map;
  }

  private List<String> parseFile(File file) {
    FileReader fr = null;
    List<String> strings = new ArrayList<>();
    try {
      fr = new FileReader(file);
    } catch (FileNotFoundException e) {
      Log.e(TAG, "parseName: " + e.getMessage());
    }
    Scanner scanner = new Scanner(fr);
    scanner.useDelimiter("\r");
    while (scanner.hasNext()) {
      strings.add(scanner.next().toLowerCase());
    }
    return strings;

  }

  /**
   * Loader method that returns the data path for the tessaract api engine, if the data is not in the tesseract
   * folder the method will create the folder and save the save the data to a "traineddate.eng" file
   * @return String path containing the tesseract data folder
   */
  public String getTessDataPath() {
    File dir = new File(APP_PATH + TESS_DATA_DIRECTORY);
    if (!dir.exists()) {
      boolean created = dir.mkdir();
      if (created) {
        Log.d(TAG, "Created Directory: " + dir.getPath());
      } else {
        Log.e(TAG, "Failed to create tessdata folder");
      }
    }
    File file = new File(APP_PATH + TESS_DATA_DIRECTORY + "eng.traineddata");
    if (!file.exists()) {
      try {
        InputStream is = applicationContext.getResources().openRawResource(R.raw.eng);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        OutputStream outputStream = new FileOutputStream(file);
        outputStream.write(buffer);
        Log.d(TAG, "Tessdata written to file: " + file.getAbsolutePath());
      } catch (IOException e) {
        Log.e(TAG,
            "Error writing tessdata to file: " + file.getAbsolutePath() + " " + e.getMessage());
      }
    }
    return APP_PATH;
  }

  /**
   * Help method to get the image file directory or create it if it doesn't exist.
   * @return File corresponding to the image directory.
   */
  public File getImagePath() {
    File dir = new File(APP_PATH + IMAGE_DIRECTORY);
    if (!dir.exists()) {
      dir.mkdir();
    }
    return new File(dir.getAbsolutePath(), System.currentTimeMillis() + ".png");
  }

  public void reset() {
    fileData.setValue(null);
  }

  private static class InstanceHolder {

    private static final FileManagerService INSTANCE = new FileManagerService();
  }

}


