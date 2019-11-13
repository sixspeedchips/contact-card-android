package io.libsoft.contactcard.controller;


import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import io.libsoft.contactcard.R;
import io.libsoft.contactcard.controller.camera.CameraCaptureListener;
import io.libsoft.contactcard.controller.camera.CameraCaptureSessionStateCallback;
import io.libsoft.contactcard.controller.camera.CameraStateCallback;
import io.libsoft.contactcard.controller.camera.CameraSurfaceTextureListener;
import java.util.Collections;

public class CameraFragment extends Fragment {

  private static final int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;
  private final int REQUEST_CODE = 1033;
  private final String LOG_TAG = "camerafragment";
  private Context context;
  private View view;
  private TextureView textureView;
  private CameraManager cameraManager;
  private CameraDevice cameraDevice;
  private String cameraId;
  private Size imageDimensions;
  private CameraStateCallback stateCallback;
  private Builder captureRequestBuilder;
  private CameraCaptureSessionStateCallback sessionStateCallback;
  private CameraCaptureSession cameraCaptureSession;
  private Handler mBackgroundHandler;
  private HandlerThread mBackgroundThread;
  private CameraCaptureListener captureListener;
  private CameraSurfaceTextureListener surfaceTextureListener;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    context = inflater.getContext();
    view = inflater.inflate(R.layout.fragment_camera, container, false);

    textureView = view.findViewById(R.id.camera_preview);
    cameraManager = ((CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE));
    initListeners();
    return view;
  }

  private void initListeners() {
    surfaceTextureListener = new CameraSurfaceTextureListener()
        .setOnComplete(this::openCamera)
        .setTextureUpdatedListener((surfaceTexture) -> {
        });
    textureView.setSurfaceTextureListener(surfaceTextureListener);
    stateCallback = new CameraStateCallback((cameraDevice) -> {
      Log.d(LOG_TAG, "opened");
      this.cameraDevice = cameraDevice;
      createCameraPreview();
    });
    sessionStateCallback = new CameraCaptureSessionStateCallback();
    sessionStateCallback.setOnConfigured((session) -> {
      if (cameraDevice != null) {
        cameraCaptureSession = session;
        updatePreview();
      }
    });
    captureListener = new CameraCaptureListener();
    captureListener.setOnCompleted(() -> {

      Log.d(LOG_TAG, String.valueOf(textureView.getBitmap().getPixel(0, 0)));
    });


  }

  private void updatePreview() {
    Log.d(LOG_TAG, "updating preview");
    if (cameraDevice != null) {
      captureRequestBuilder
          .set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
      try {
        Log.d(LOG_TAG, "BUILDER: " + captureRequestBuilder.toString());
        Log.d(LOG_TAG, "SESSION: " + cameraCaptureSession.toString());
        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(),
            captureListener, mBackgroundHandler);
      } catch (CameraAccessException e) {
        e.printStackTrace();
      }
    }
  }

  private void createCameraPreview() {

    SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
    surfaceTexture.setDefaultBufferSize(imageDimensions.getWidth(), imageDimensions.getHeight());
    Surface surface = new Surface(surfaceTexture);
    try {
      captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
      captureRequestBuilder.addTarget(surface);
      cameraDevice
          .createCaptureSession(Collections.singletonList(surface), sessionStateCallback, null);

    } catch (CameraAccessException e) {
      e.printStackTrace();
    }


  }

  private void openCamera() {
    Log.d(LOG_TAG, "opening camera");
    try {
      cameraId = cameraManager.getCameraIdList()[0];
      CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
      StreamConfigurationMap map = characteristics
          .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
      imageDimensions = map.getOutputSizes(SurfaceTexture.class)[0];
      if (ActivityCompat.checkSelfPermission(context, permission.CAMERA) == PERMISSION_GRANTED
          && ActivityCompat.checkSelfPermission(context, permission.WRITE_EXTERNAL_STORAGE)
          == PERMISSION_GRANTED) {
        cameraManager.openCamera(cameraId, stateCallback, null);

      } else {
        ActivityCompat.requestPermissions(getActivity(),
            new String[]{
                permission.CAMERA,
                permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_CODE);
      }

    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }


  @Override
  public void onResume() {
    super.onResume();
    Log.d(LOG_TAG, "starting background thread");
    startBackgroundThread();
    if (textureView.isAvailable()) {
      openCamera();
    } else {
      textureView.setSurfaceTextureListener(surfaceTextureListener);
    }
  }

  @Override
  public void onPause() {
    stopBackgroundThread();
    super.onPause();
  }

  private void startBackgroundThread() {
    mBackgroundThread = new HandlerThread("camera_background");
    mBackgroundThread.start();
    mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
  }

  private void stopBackgroundThread() {
    mBackgroundThread.quitSafely();
    try {
      mBackgroundThread.join();
      mBackgroundThread = null;
      mBackgroundHandler = null;
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
