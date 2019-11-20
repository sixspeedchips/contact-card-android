/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.controller.camera;

import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;

class CameraImageAvailableListener implements OnImageAvailableListener {


  private Completer completer;

  public CameraImageAvailableListener setOnCompleted(Completer completer) {
    this.completer = completer;
    return this;
  }


  @Override
  public void onImageAvailable(ImageReader reader) {
    completer.complete(reader);
  }


  interface Completer {

    void complete(ImageReader reader);
  }

}
