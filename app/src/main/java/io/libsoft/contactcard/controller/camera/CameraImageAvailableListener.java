package io.libsoft.contactcard.controller.camera;

import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;

public class CameraImageAvailableListener implements OnImageAvailableListener {


  private Completer completer;

  public CameraImageAvailableListener setOnCompleted(Completer completer) {
    this.completer = completer;
    return this;
  }


  @Override
  public void onImageAvailable(ImageReader reader) {
    completer.complete(reader);
  }


  public interface Completer {

    void complete(ImageReader reader);
  }

}
