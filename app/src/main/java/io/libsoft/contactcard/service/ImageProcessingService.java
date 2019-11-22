/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.service;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.googlecode.tesseract.android.TessBaseAPI;
import io.libsoft.contactcard.model.pojo.Cropper;
import io.reactivex.disposables.CompositeDisposable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Class implementing the singleton pattern, providing {@link LiveData} for the {@link
 * TessBaseAPI}.
 */
public class ImageProcessingService {

  private static final double PIXEL_AREA = 5e5;
  private static Application applicationContext;
  private final MutableLiveData<String> ocrResults;
  private final MutableLiveData<List<Rect>> candidates;
  private final CompositeDisposable pending;
  private String TAG = this.getClass().getSimpleName();
  private TessBaseAPI api;
  private Mat mat;
  private Mat grey;
  private Cropper cropped;
  private Rect medianRect;


  private ImageProcessingService() {
    api = new TessBaseAPI();
    api.init(FileManagerService.getInstance().getTessDataPath(), "eng");
    ocrResults = new MutableLiveData<>();
    candidates = new MutableLiveData<>(new ArrayList<>());

    grey = new Mat();
    mat = new Mat();
    pending = new CompositeDisposable();

  }

  /**
   * Sets the application context
   *
   * @param applicationContext
   */
  public static void setApplicationContext(Application applicationContext) {
    ImageProcessingService.applicationContext = applicationContext;
  }

  /**
   * Singleton pattern for service
   *
   * @return the singleton {@link ImageProcessingService}
   */
  public static ImageProcessingService getInstance() {
    return ImageProcessingService.InstanceHolder.INSTANCE;
  }

  /**
   * Live data for the OCR results
   * @return {@link LiveData<String>} a live data wrapper for the OCR reader outputs.
   */
  public LiveData<String> getOcrResults() {
    return ocrResults;
  }


  /**
   * Crops the {@link Bitmap} from the camera stream using the median {@link Rect} from the previous
   * X contour searches, where X is a chosen threshold.
   * @param bitmap to be cropped from the camera stream
   * @return A cropped bitmap bounded by the bounding box containing the card.
   */
  public Bitmap getCroppedImage(Bitmap bitmap) {
    Rect rect = medianRect;
    Mat saveMat = new Mat();
    Utils.bitmapToMat(bitmap.copy(Bitmap.Config.ARGB_8888, true), saveMat);
    Imgproc.rectangle(saveMat, new Point(rect.x, rect.y),
        new Point(rect.x + rect.width, rect.y + rect.height),
        new Scalar(255, 0, 0, 255), 12);
    Bitmap bm = bitmapFromMat(saveMat, rect);
    return bm;

  }


  /**
   * Performs Ocr on an image from file.
   *
   * @param file {@link File} the file to be read and parsed via OCR
   */
  public void performOcr(File file) {
    new Thread(() -> {
      api.setImage(file);
      String result = api.getUTF8Text();
      ocrResults.postValue(result);
      TextProcessorService.getInstance().process(result);
      api.clear();
    }).start();
  }


  /**
   * Attempts to find the card within the camera stream image from a {@link Bitmap}.
   *
   * @param bitmap from the camera sensor.
   * @return {@link Cropper} a Cropper, which contains the original Image bitmap, a bounding box, if
   * a card was found and the cropped bitmap image.
   */
  public Cropper findCard(Bitmap bitmap, Size bound) {

    Utils.bitmapToMat(bitmap.copy(Bitmap.Config.ARGB_8888, true), mat);
    Imgproc.cvtColor(mat, grey, Imgproc.COLOR_RGB2GRAY);
    Imgproc.GaussianBlur(grey, grey, new Size(3, 3), .8);
    Imgproc.Canny(grey, grey, 50, 100);
    cropped = mapContours(grey, mat, bound.area() / 30);
//    cropped = Cropper.of(bitmapFromMat(grey),null,null);
    return cropped;
  }

  /**
   * Method to parse a file and return its bitmap representation.
   *
   * @param file to be loaded.
   * @return {@link Bitmap} loaded from the file.
   */
  public Bitmap fileToBitmap(String file) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    return BitmapFactory.decodeFile(file, options);
  }

  private Cropper mapContours(Mat filteredMat, Mat outputMat, double bound) {
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(filteredMat, contours, new Mat(), Imgproc.RETR_LIST,
        Imgproc.CHAIN_APPROX_SIMPLE);

    for (MatOfPoint contour : contours) {
      MatOfPoint2f new_mat = new MatOfPoint2f(contour.toArray());
      int contourSize = (int) contour.total();
      MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
      Imgproc.approxPolyDP(new_mat, approxCurve_temp, contourSize * .2, true);

      if (approxCurve_temp.total() >= 4) {
        MatOfPoint points = new MatOfPoint(approxCurve_temp.toArray());
        Rect rect = Imgproc.boundingRect(points);
        if (rect.area() > bound) {
          Imgproc.rectangle(outputMat, new Point(rect.x, rect.y),
              new Point(rect.x + rect.width, rect.y + rect.height),
              new Scalar(255, 0, 0, 255), 15);
          candidates.getValue().add(rect);
          medianRect = medianRect();
          candidates.postValue(candidates.getValue());
          return Cropper.of(bitmapFromMat(outputMat), rect, bitmapFromMat(outputMat, rect));
        }
      }
    }
    return Cropper.of(bitmapFromMat(outputMat), null, null);
  }

  private Bitmap bitmapFromMat(Mat mat, Rect rect) {
    return bitmapFromMat(mat.submat(rect));
  }

  private Bitmap bitmapFromMat(Mat mat) {
    Bitmap bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Config.ARGB_8888);
    Utils.matToBitmap(mat, bmp);
    return bmp;
  }

  /**
   * Returns the live data implementation of the {@link Rect} candidates for cropping the stream
   * image.
   *
   * @return Live data of {@link List<Rect>} containing an accumulation of Rects.
   */
  public LiveData<List<Rect>> getCandidates() {
    return candidates;
  }

  private Rect maxRect() {
    candidates.getValue().sort(Comparator.comparingDouble(Rect::area));
    return candidates.getValue().get(candidates.getValue().size() - 1);
  }

  private Rect medianRect() {
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
    Rect crop = new Rect(
        new Point(topX.get(topX.size() / 2) - offset, topY.get(topY.size() / 2) - offset),
        new Point(bottomX.get(bottomX.size() / 2) + offset,
            bottomY.get(bottomY.size() / 2) + offset));

    Log.d(TAG, "medianRect: " + crop.toString());
    return crop;
  }


  /**
   * Clears the current stack of captured bounding boxes.
   */
  public void clear() {
    Objects.requireNonNull(getCandidates().getValue()).clear();
    ;
    Log.d(TAG, "clear: " + getCandidates().getValue().isEmpty());
  }

  private static class InstanceHolder {

    private static final ImageProcessingService INSTANCE = new ImageProcessingService();

  }
}
