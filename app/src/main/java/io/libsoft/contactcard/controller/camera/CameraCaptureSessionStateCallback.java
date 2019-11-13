package io.libsoft.contactcard.controller.camera;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.support.annotation.NonNull;

public class CameraCaptureSessionStateCallback extends StateCallback {


  private Configured configuration;

  public void setOnConfigured(Configured configuration) {
    this.configuration = configuration;
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
