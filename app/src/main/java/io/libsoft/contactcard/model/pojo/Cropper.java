/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.model.pojo;

import android.graphics.Bitmap;
import javax.annotation.Nullable;
import org.opencv.core.Rect;


/**
 * Used to hold the results of a round of image processing.
 */
public class Cropper {

  private Bitmap original;
  private Rect rect;
  private Bitmap cropped;

  private Cropper(Bitmap bitmap, Rect rect, Bitmap cropped) {
    this.original = bitmap;
    this.rect = rect;
    this.cropped = cropped;
  }

  /**
   * Returns the original image {@link Bitmap} with a bounding rectangle on a detected card.
   * @return Original Bitmap.
   */
  public Bitmap getOriginal() {
    return original;
  }

  /**
   * Getter for the bounding {@link Rect} containing the card.
   * @return Rect bounding the card, or null if none was found.
   */
  public Rect getRect() {
    return rect;
  }


  /**
   * The {@link Bitmap} of a cropped image containing the card image.
   * @return bitmap of a cropped card.
   */
  public Bitmap getCropped() {
    return cropped;
  }

  /**
   * Static factory method for creating a new Cropped class
   * @param bitmap Original bitmap, may contain rectangle overlays.
   * @param rect Rectangle bounding box of card.
   * @param cropped Bitmap of a cropped card image.
   * @return A new immutable instance of a {@link Cropper} class.
   */
  public static Cropper of(Bitmap bitmap, @Nullable Rect rect, @Nullable Bitmap cropped){
    return new Cropper(bitmap, rect, cropped);
  }
}
