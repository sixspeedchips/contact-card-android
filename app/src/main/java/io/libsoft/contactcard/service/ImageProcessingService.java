package io.libsoft.contactcard.service;

import android.app.Application;
import android.media.Image;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.googlecode.tesseract.android.TessBaseAPI;
import java.io.File;
import java.nio.ByteBuffer;

public class ImageProcessingService {

  private static String TAG = ImageProcessingService.class.getSimpleName();
  private static Application applicationContext;
  private static TessBaseAPI api;

  static {
    api = new TessBaseAPI();
    api.init(FileManagerService.getInstance().getTessDataPath(), "eng");
  }

  private final MutableLiveData<String> ocrResults;


  public ImageProcessingService() {
    ocrResults = new MutableLiveData<>();
  }

  public static void setApplicationContext(Application applicationContext) {
    ImageProcessingService.applicationContext = applicationContext;
  }

  public static ImageProcessingService getInstance() {
    return ImageProcessingService.InstanceHolder.INSTANCE;
  }

  public LiveData<String> getOcrResults() {
    return ocrResults;
  }

  public void cropImage(Image im) {
    ByteBuffer buffer = im.getPlanes()[0].getBuffer();
    byte[] bytes = new byte[buffer.capacity()];

    buffer.get(bytes);
  }

  public void performOcr(File file) {
    new Thread(() -> {
      api.setImage(file);
      ocrResults.postValue(api.getUTF8Text());
      api.clear();
    }).start();
  }


  private static class InstanceHolder {

    private static final ImageProcessingService INSTANCE = new ImageProcessingService();
  }
}
