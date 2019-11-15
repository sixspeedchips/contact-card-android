package io.libsoft.contactcard.controller;


import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
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
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.googlecode.tesseract.android.TessBaseAPI;
import io.libsoft.contactcard.R;
import io.libsoft.contactcard.controller.camera.CameraCaptureListener;
import io.libsoft.contactcard.controller.camera.CameraCaptureSessionStateCallback;
import io.libsoft.contactcard.controller.camera.CameraStateCallback;
import io.libsoft.contactcard.controller.camera.CameraSurfaceTextureListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.opencv.core.Mat;

//import org.bytedeco.leptonica.PIX;
//
//import org.bytedeco.leptonica.global.lept;
//import org.bytedeco.tesseract.TessBaseAPI;
//import org.bytedeco.javacpp.lept;
//import org.bytedeco.javacpp.lept.PIX;
//import org.bytedeco.javacpp.tesseract.TessBaseAPI;


public class CameraFragment extends Fragment {


  private static final int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;

//  static {
//    System.loadLibrary("opencv_java");
//  }

  private final int REQUEST_CODE = 1033;
  private final String LOG_TAG = "camerafragment";
  private Context context;
  private View view;
  private TextureView textureView;
  private CameraManager manager;
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
  private FloatingActionButton fab;
  private Surface surface;
  private File file;
  private CameraCharacteristics characteristics;
  private TessBaseAPI api;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    context = inflater.getContext();
    view = inflater.inflate(R.layout.fragment_camera, container, false);

    textureView = view.findViewById(R.id.camera_preview);
    fab = view.findViewById(R.id.fab);
    manager = ((CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE));
    initListeners();
    initTessApi();

    return view;
  }

  private void initTessApi() {
    String dataPath = getActivity().getExternalFilesDir(null).toString();
    File dir = new File(dataPath + "/tessdata/");
    File file = new File(dataPath + "/tessdata/" + "eng.traineddata");
    if (!dir.exists()) {
      Log.d(LOG_TAG, "Created Directory: " + dir.mkdir());
    }
    if (!file.exists()) {
      try {
        InputStream is = getResources().openRawResource(R.raw.eng);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        OutputStream outputStream = new FileOutputStream(file);
        outputStream.write(buffer);
        Log.d(LOG_TAG, "Wrote to file: " + file.getAbsolutePath());
      } catch (IOException e) {
        Log.e(LOG_TAG, "Error Writing to file: " + file.getAbsolutePath() + e.getMessage());
      }
    }
    Log.d(LOG_TAG, file.getAbsolutePath());
    api = new TessBaseAPI();
    api.init(dataPath, "eng");
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

    });
    fab.setOnClickListener((view) -> {
      takePicture();
    });

  }

  private void updatePreview() {
    Log.d(LOG_TAG, "updating preview");
    if (cameraDevice != null) {
      captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
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
    surface = new Surface(surfaceTexture);
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
      cameraId = manager.getCameraIdList()[0];
      characteristics = manager.getCameraCharacteristics(cameraId);
      StreamConfigurationMap map = characteristics
          .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
      imageDimensions = map.getOutputSizes(SurfaceTexture.class)[0];
      if (ActivityCompat.checkSelfPermission(context, permission.CAMERA) == PERMISSION_GRANTED
          && ActivityCompat.checkSelfPermission(context, permission.WRITE_EXTERNAL_STORAGE)
          == PERMISSION_GRANTED) {
        manager.openCamera(cameraId, stateCallback, null);

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


  private void takePicture() {
    if (null == cameraDevice) {
      Log.e(LOG_TAG, "cameraDevice is null");
      return;
    }

    try {
      Log.d(LOG_TAG, "Capturing");

      ImageReader reader = ImageReader
          .newInstance(imageDimensions.getWidth(), imageDimensions.getWidth(), ImageFormat.JPEG, 1);
      List<Surface> outputSurfaces = new ArrayList<>(2);
      outputSurfaces.add(reader.getSurface());

      Builder captureBuilder = cameraDevice
          .createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
      captureBuilder.addTarget(reader.getSurface());
      captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
      captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,
          characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION));

      File dir = new File(getActivity().getExternalFilesDir(null) + "/images/");
      if (!dir.exists()) {
        dir.mkdir();
      }
      file = new File(dir.getAbsolutePath(), System.currentTimeMillis() + ".jpg");
      Log.d(LOG_TAG, "saving file at: " + file);
      ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
          Log.d(LOG_TAG, "image available");
          Image image = null;
          try {
            image = reader.acquireLatestImage();
            Mat mat = new Mat();

            ByteBuffer buffer = image.getPlanes()[0].getBuffer();

            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            mat.put(0, 0, bytes);
            System.out.println("HERE: " + mat.toString());
            Log.d(LOG_TAG, "Captured: " + Arrays.toString(mat.get(0, 0)));
            save(bytes);
            String out = getOCRResult(bytes);
            Log.d(LOG_TAG, out);

            Toast.makeText(context,"Text: " + out, Toast.LENGTH_LONG).show();

          } catch (IOException e) {
            e.printStackTrace();
          } finally {
            if (image != null) {
              image.close();
            }
          }
        }

        private void save(byte[] bytes) throws IOException {
          OutputStream output = null;
          try {
            output = new FileOutputStream(file);
            output.write(bytes);
          } finally {
            if (output != null) {
              output.close();
            }
          }
        }
      };

      reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);

      final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
          super.onCaptureCompleted(session, request, result);
          Toast.makeText(context, "Saved:" + file, Toast.LENGTH_SHORT).show();
          createCameraPreview();
        }
      };
      cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSessionStateCallback().setOnConfigured((session)->{
        try {
          session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
          Log.d(LOG_TAG, "configuration successful");
        } catch (CameraAccessException e) {
          Log.e(LOG_TAG, "Configuration failed: " + e.getMessage());
        }
      }), mBackgroundHandler);
    } catch (CameraAccessException e) {
      Log.e(LOG_TAG, "failed to capture: " + e.getMessage());
    }
  }


  public String getOCRResult(byte[] bytes) {
//    Log.d(LOG_TAG, api.toString());
//    api.setImage(file);
//    api.setImage(bytes, imageDimensions.getWidth(), imageDimensions.getHeight(), 4,4*imageDimensions.getWidth());

    return "";
//    return api.getUTF8Text();
  }

  private void unlockFocus() {
    try {
      captureRequestBuilder
          .set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
      captureRequestBuilder
          .set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
      cameraCaptureSession
          .setRepeatingRequest(captureRequestBuilder.build(), captureListener, mBackgroundHandler);

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
