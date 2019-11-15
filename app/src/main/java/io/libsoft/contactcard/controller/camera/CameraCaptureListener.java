package io.libsoft.contactcard.controller.camera;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import androidx.annotation.NonNull;

public class CameraCaptureListener extends CaptureCallback {

  private final String LOG_TAG = getClass().getSimpleName();
  private OnCapture onCapture;

  @Override
  public void onCaptureCompleted(@NonNull CameraCaptureSession session,
      @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
    super.onCaptureCompleted(session, request, result);
//    Log.d("KEYS",request.getKeys().toString());
    onCapture.complete();
//    Log.d(LOG_TAG,result.get(CaptureResult.SENSOR_TIMESTAMP).toString());
//    result.get()
  }

  public void setOnCompleted(OnCapture onCapture) {
    this.onCapture = onCapture;
  }

  public interface OnCapture {

    void complete();
  }
}
