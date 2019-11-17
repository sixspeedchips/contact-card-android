package io.libsoft.contactcard.service;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.googlecode.tesseract.android.TessBaseAPI;
import io.libsoft.contactcard.pojo.RectFinder;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

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

  public void cropImage(File file) {
//    ByteBuffer buffer = im.getPlanes()[0].getBuffer();
//    byte[] bytes = new byte[buffer.capacity()];
//
//    buffer.get(bytes);
    Mat mat = Imgcodecs.imread(file.getAbsolutePath());
    Log.d(TAG, "cropImage: " + Arrays.toString(mat.get(0, 0)));
    Imgproc.drawMarker(mat, new Point(100,100),new Scalar(0,0,255), Imgproc.MARKER_DIAMOND,100);
    Imgcodecs.imwrite(FileManagerService.getInstance().getImageDirectory().toString(),mat);
  }




  public void cropImage(Bitmap bitmap){

    Mat mat = new Mat();
    Utils.bitmapToMat(bitmap.copy(Bitmap.Config.ARGB_8888, true), mat);
//    Imgcodecs.imwrite(FileManagerService.getInstance().getImageDirectory().toString() + ".png",mat);

  }

  public void performOcr(File file) {
    new Thread(() -> {
      api.setImage(file);
      ocrResults.postValue(api.getUTF8Text());
      api.clear();
    }).start();
  }

  public RectFinder findSquare(Bitmap bitmap) {
    Mat mat = new Mat();
    Mat grey = new Mat();
    Utils.bitmapToMat(bitmap.copy(Bitmap.Config.ARGB_8888, true), mat);
    Bitmap bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Config.ARGB_8888);
    Imgproc.cvtColor(mat, grey, Imgproc.COLOR_RGB2GRAY);
    Imgproc.GaussianBlur(grey, grey, new Size(3,3),.8 );
    Imgproc.Canny(grey, grey,10, 150);
    Rect rect = mapContours(grey,mat);
    Utils.matToBitmap(mat,bmp);
    if (rect != null){
      Mat sub = mat.submat(rect);
      bmp = Bitmap.createBitmap(sub.cols(), sub.rows(), Config.ARGB_8888);
      Utils.matToBitmap(sub,bmp);
      return RectFinder.of(bmp,rect);
    } else {
      return RectFinder.of(bmp, null);
    }
  }

  private Rect mapContours(Mat filtered,Mat mat){
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(filtered, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

    for (int i = 0; i < contours.size(); i++) {
      MatOfPoint contour = contours.get(i);
      MatOfPoint2f new_mat = new MatOfPoint2f(contour.toArray());
      int contourSize = (int) contour.total();
      MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
      Imgproc.approxPolyDP(new_mat, approxCurve_temp, contourSize*.1, true);

      if (approxCurve_temp.total()==4) {
        MatOfPoint points = new MatOfPoint(approxCurve_temp.toArray());
        Rect rect = Imgproc.boundingRect(points);
        if (rect.area() > 3e5){
          Imgproc.rectangle(mat, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(255,0,0,255), 10);
          return rect;
        }
      }
    }

    return null;
  }


  private static class InstanceHolder {

    private static final ImageProcessingService INSTANCE = new ImageProcessingService();
  }
}
