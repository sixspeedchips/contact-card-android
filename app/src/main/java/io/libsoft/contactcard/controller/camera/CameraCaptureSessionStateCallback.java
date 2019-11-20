/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.controller.camera;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import androidx.annotation.NonNull;

class CameraCaptureSessionStateCallback extends StateCallback {


  private Configured configuration;

  CameraCaptureSessionStateCallback setOnConfigured(Configured configuration) {
    this.configuration = configuration;
    return this;
  }


  @Override
  public void onConfigured(@NonNull CameraCaptureSession session) {
    configuration.complete(session);
  }

  @Override
  public void onConfigureFailed(@NonNull CameraCaptureSession session) {

  }

  public interface Configured {

    void complete(CameraCaptureSession session);
  }
}
