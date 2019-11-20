/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.controller.camera;

import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraDevice.StateCallback;
import androidx.annotation.NonNull;

class CameraStateCallback extends StateCallback {


  private OpenedCompleter onOpened;
  private ClosedCompleter onClosed;

  CameraStateCallback(OpenedCompleter onOpened, ClosedCompleter onClosed) {
    this.onOpened = onOpened;
    this.onClosed = onClosed;
  }

  @Override
  public void onOpened(@NonNull CameraDevice camera) {

    onOpened.complete(camera);
  }

  @Override
  public void onDisconnected(@NonNull CameraDevice camera) {
    onClosed.complete(camera);
  }

  @Override
  public void onError(@NonNull CameraDevice camera, int error) {

  }

  public interface OpenedCompleter {

    void complete(CameraDevice cameraDevice);
  }

  public interface ClosedCompleter {

    void complete(CameraDevice cameraDevice);
  }
}
