package io.libsoft.contactcard.service;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.CountDownTimer;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.googlecode.tesseract.android.TessBaseAPI;
import io.libsoft.contactcard.model.pojo.Cropper;
import io.reactivex.disposables.CompositeDisposable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImageProcessingService {

  private static final double PIXEL_AREA = 5e5;
  private static Application applicationContext;
  private String TAG = this.getClass().getSimpleName();
  private TessBaseAPI api;
  private Timer timer;

  private final MutableLiveData<String> ocrResults;
  private final MutableLiveData<List<Rect>> candidates;

  private Mat mat;
  private Mat grey;
  private Cropper cropped;
  private final CompositeDisposable pending;


  public ImageProcessingService() {
    api = new TessBaseAPI();
    api.init(FileManagerService.getInstance().getTessDataPath(), "eng");
    ocrResults = new MutableLiveData<>();
    candidates = new MutableLiveData<>(new ArrayList<>());

    grey = new Mat();
    mat = new Mat();

    pending = new CompositeDisposable();
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

  public Bitmap getCroppedImage(Bitmap bitmap) {

      Rect rect = medianRect();
      candidates.getValue().clear();
      Mat saveMat = new Mat();
      Utils.bitmapToMat(bitmap.copy(Bitmap.Config.ARGB_8888, true), saveMat);
      Log.d(TAG, "saveCroppedImage: "+ saveMat.empty());
      Imgproc.rectangle(saveMat, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),
          new Scalar(255,0,0,255), 12);
      Bitmap bm = bitmapFromMat(saveMat, rect);
      return bm;

  }






  public void performOcr(File file) {
    new Thread(() -> {
      api.setImage(file);
      ocrResults.postValue(api.getUTF8Text());
      api.clear();
    }).start();
  }

  public Cropper findCard(Bitmap bitmap) {

    Utils.bitmapToMat(bitmap.copy(Bitmap.Config.ARGB_8888, true), mat);
    Imgproc.cvtColor(mat, grey, Imgproc.COLOR_RGB2GRAY);
    Imgproc.GaussianBlur(grey, grey, new Size(3,3),.8 );
    Imgproc.Canny(grey, grey,5, 180);
    cropped = mapContours(mat, grey);
//    cropped = Cropper.of(bitmapFromMat(grey),null,null);
    return cropped;
  }

  private Cropper mapContours(Mat outputMat, Mat filteredMat){
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(filteredMat, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

    for (int i = 0; i < contours.size(); i++) {
      MatOfPoint contour = contours.get(i);
      MatOfPoint2f new_mat = new MatOfPoint2f(contour.toArray());
      int contourSize = (int) contour.total();
      MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
      Imgproc.approxPolyDP(new_mat, approxCurve_temp, contourSize*.2, true);

      if (approxCurve_temp.total()==4) {
        MatOfPoint points = new MatOfPoint(approxCurve_temp.toArray());
        Rect rect = Imgproc.boundingRect(points);
        if (rect.area() > PIXEL_AREA){
          Imgproc.rectangle(outputMat, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),
              new Scalar(255,0,0,255), 15);
          candidates.getValue().add(rect);
          candidates.postValue(candidates.getValue());
          Log.d(TAG, "mapContours: rect found");
          return Cropper.of(bitmapFromMat(outputMat), rect, bitmapFromMat(outputMat,rect));
        }
      }
    }
    return Cropper.of(bitmapFromMat(outputMat), null, null);
  }

  private Bitmap bitmapFromMat(Mat mat, Rect rect){
    return bitmapFromMat(mat.submat(rect));
  }

  private Bitmap bitmapFromMat(Mat mat){
    Bitmap bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Config.ARGB_8888);
    Utils.matToBitmap(mat,bmp);
    return bmp;
  }


  public LiveData<List<Rect>> getCandidates() {
    return candidates;
  }

  private Rect medianRect(){
    int offset = 10;
    List<Integer> topX = new ArrayList<>();
    List<Integer> topY = new ArrayList<>();
    List<Integer> bottomX = new ArrayList<>();
    List<Integer> bottomY = new ArrayList<>();

    for (Rect rect : candidates.getValue()) {
      topX.add(rect.x);
      topY.add(rect.y);
      bottomX.add(rect.x + rect.width);
      bottomY.add(rect.y + rect.height);
    }
    Collections.sort(topX);
    Collections.sort(topY);
    Collections.sort(bottomX);
    Collections.sort(bottomY);
    Rect crop = new Rect(new Point(topX.get(topX.size()/2)-offset,topY.get(topY.size()/2)-offset),
        new Point(bottomX.get(bottomX.size()/2)+offset, bottomY.get(bottomY.size()/2)+offset));

    Log.d(TAG, "medianRect: " + crop.toString());
    return crop;
  }

  private static class Timer extends CountDownTimer{

    /**
     * @param millisInFuture    The number of millis in the future from the call to {@link #start()}
     *                          until the countdown is done and {@link #onFinish()} is called.
     * @param countDownInterval The interval along the way to receive {@link #onTick(long)} callbacks.
     */
    public Timer(long millisInFuture, long countDownInterval) {
      super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {

    }

    @Override
    public void onFinish() {

    }
  }




  private static class InstanceHolder {

    private static final ImageProcessingService INSTANCE = new ImageProcessingService();

  }
}
