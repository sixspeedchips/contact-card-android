package io.libsoft.contactcard.pojo;

import android.graphics.Bitmap;
import org.opencv.core.Rect;

public class RectFinder {

  private Bitmap bitmap;
  private Rect rect;

  private RectFinder(Bitmap bitmap, Rect rect) {
    this.bitmap = bitmap;
    this.rect = rect;
  }

  public Bitmap getBitmap() {
    return bitmap;
  }

  public Rect getRect() {
    return rect;
  }

  public static RectFinder of(Bitmap bitmap, Rect rect){
    return new RectFinder(bitmap, rect);
  }
}
