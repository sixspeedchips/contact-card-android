package io.libsoft.contactcard.controller.camera;

import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraDevice.StateCallback;
import android.support.annotation.NonNull;

public class CameraStateCallback extends StateCallback {


  private OnOpened onOpened;

  public CameraStateCallback(OnOpened onOpened) {
    this.onOpened = onOpened;
  }

  @Override
  public void onOpened(@NonNull CameraDevice camera) {
    onOpened.complete(camera);
  }

  @Override
  public void onDisconnected(@NonNull CameraDevice camera) {

  }

  @Override
  public void onError(@NonNull CameraDevice camera, int error) {

  }

  public interface OnOpened {

    void complete(CameraDevice cameraDevice);
  }
}
