package io.libsoft.contactcard.service;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Vibrator;
import android.util.Log;
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


public class FileManagerService {

  private static final String TESS_DATA_DIRECTORY = "/tessdata/";
  private static final String IMAGE_DIRECTORY = "/images/";
  private static final String NAMES_DIRECTORY = "/names/";
  private static Application applicationContext;
  private static String TAG = "FileManagerService";
  private static String APP_PATH;
  private static Vibrator vibrator;

  public FileManagerService() {
    vibrator = (Vibrator) applicationContext.getSystemService(Context.VIBRATOR_SERVICE);
  }

  public static void setApplicationContext(Application applicationContext) {
    FileManagerService.applicationContext = applicationContext;
    APP_PATH = applicationContext.getExternalFilesDir(null).getPath();
  }


  public static FileManagerService getInstance() {
    return FileManagerService.InstanceHolder.INSTANCE;
  }


  public File saveImage(Bitmap bitmap) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bitmap.compress(CompressFormat.PNG, 100, stream);
    byte[] bytes = stream.toByteArray();
    bitmap.recycle();
    return saveImage(bytes);
  }

  public File saveImage(byte[] bytes) {

    File file = getImageDirectory();
    try (OutputStream output = new FileOutputStream(file)) {
      output.write(bytes);
      Image image = new Image();
      image.setRaw(false);
      image.setUrl(file.getAbsolutePath());
      ContactDatabase.getInstance().getImageDao().insert(image);
      vibrator.vibrate(200);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return file;
  }


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
    scanner.useDelimiter("\n");
    while (scanner.hasNext()) {
      strings.add(scanner.next().toLowerCase());
    }
    System.out.println(strings);
    return strings;

  }

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

  public File getImageDirectory() {
    File dir = new File(APP_PATH + IMAGE_DIRECTORY);
    if (!dir.exists()) {
      dir.mkdir();
    }
    return new File(dir.getAbsolutePath(), System.currentTimeMillis() + ".png");
  }

  private static class InstanceHolder {

    private static final FileManagerService INSTANCE = new FileManagerService();
  }

}


