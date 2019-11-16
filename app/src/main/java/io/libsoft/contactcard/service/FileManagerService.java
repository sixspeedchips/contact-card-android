package io.libsoft.contactcard.service;

import android.app.Application;
import android.content.Context;
import android.media.Image;
import android.os.Vibrator;
import android.util.Log;
import io.libsoft.contactcard.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class FileManagerService {

  private static final String TESS_DATA_FOLDER = "/tessdata/";
  private static final String IMAGE_DIRECTORY = "/images/";
  private static Application applicationContext;
  private static String LOG_TAG = FileManagerService.class.getSimpleName();
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

  public String saveImage(Image image) {

    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
    byte[] bytes = new byte[buffer.capacity()];
    buffer.get(bytes);

    String writeSuccessful = null;
    File dir = new File(APP_PATH + IMAGE_DIRECTORY);
    if (!dir.exists()) {
      dir.mkdir();
    }
    File file = new File(dir.getAbsolutePath(), System.currentTimeMillis() + ".jpg");
    try (OutputStream output = new FileOutputStream(file)) {
      output.write(bytes);
      writeSuccessful = file.getAbsolutePath();
      vibrator.vibrate(200);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // perform OCR need to move to different service
    ImageProcessingService.getInstance().doOcr(file);
    return writeSuccessful;
  }


  public String getTessDataPath() {
    File dir = new File(APP_PATH + "/tessdata/");
    if (!dir.exists()) {
      boolean created = dir.mkdir();
      if (created) {
        Log.d(LOG_TAG, "Created Directory: " + dir.getPath());
      } else {
        Log.e(LOG_TAG, "Failed to create tessdata folder");
      }
    } else {
      File file = new File(APP_PATH + TESS_DATA_FOLDER + "eng.traineddata");
      if (!file.exists()) {
        try {
          InputStream is = applicationContext.getResources().openRawResource(R.raw.eng);
          byte[] buffer = new byte[is.available()];
          is.read(buffer);
          OutputStream outputStream = new FileOutputStream(file);
          outputStream.write(buffer);
          Log.d(LOG_TAG, "Tessdata written to file: " + file.getAbsolutePath());
        } catch (IOException e) {
          Log.e(LOG_TAG,
              "Error writing tessdata to file: " + file.getAbsolutePath() + " " + e.getMessage());
        }
      }
    }

    return APP_PATH;
  }

  private static class InstanceHolder {

    private static final FileManagerService INSTANCE = new FileManagerService();
  }
}
