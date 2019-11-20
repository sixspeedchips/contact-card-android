/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.controller.camera;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import androidx.annotation.NonNull;

class CameraCaptureListener extends CaptureCallback {

  private final String TAG = getClass().getSimpleName();
  private OnCapture onCapture;

  @Override
  public void onCaptureCompleted(@NonNull CameraCaptureSession session,
      @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
    super.onCaptureCompleted(session, request, result);
    onCapture.complete();
  }

  CameraCaptureListener setOnCompleted(OnCapture onCapture) {
    this.onCapture = onCapture;
    return this;
  }

  interface OnCapture {

    void complete();
  }
}
