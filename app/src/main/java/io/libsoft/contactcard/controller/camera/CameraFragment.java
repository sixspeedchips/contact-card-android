package io.libsoft.contactcard.controller.camera;


import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import io.libsoft.contactcard.R;
import io.libsoft.contactcard.model.pojo.Cropper;
import io.libsoft.contactcard.service.FileManagerService;
import io.libsoft.contactcard.service.ImageProcessingService;
import io.libsoft.contactcard.viewmodel.MainViewModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CameraFragment extends Fragment {


  private static final int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;


  private final int REQUEST_CODE = 1033;
  private final String TAG = "camerafragment";


  private Context context;
  private View view;
  private TextureView textureView;
  private ImageView processedView;

  private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

  static {
    ORIENTATIONS.append(Surface.ROTATION_0, 90);
    ORIENTATIONS.append(Surface.ROTATION_90, 0);
    ORIENTATIONS.append(Surface.ROTATION_180, 270);
    ORIENTATIONS.append(Surface.ROTATION_270, 180);
  }

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
  private CameraCharacteristics characteristics;
  private FileManagerService fileManagerService;
  private ImageReader reader;
  private MainViewModel viewModel;



  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    context = inflater.getContext();
    view = inflater.inflate(R.layout.fragment_camera, container, false);


    fileManagerService = FileManagerService.getInstance();
    manager = ((CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE));

    initViews();
    initViewModel();
    initListeners();
    return view;
  }

  private void initViewModel() {
    viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    getLifecycle().addObserver(viewModel);
  }

  private void initViews() {
    textureView = view.findViewById(R.id.camera_preview);
    processedView = view.findViewById(R.id.processed_image);
    fab = view.findViewById(R.id.fab);
  }

  private void initListeners() {
    surfaceTextureListener = new CameraSurfaceTextureListener()
        .setOnComplete(this::openCamera)
        .setTextureUpdatedListener((surfaceTexture) -> {
        });
    textureView.setSurfaceTextureListener(surfaceTextureListener);
    stateCallback = new CameraStateCallback((cameraDevice) -> {
      Log.d(TAG, "opened");
      this.cameraDevice = cameraDevice;
      createCameraPreview();
    }, (cameraDevice) -> {
      closeCamera();
    });
    sessionStateCallback = new CameraCaptureSessionStateCallback();
    sessionStateCallback.setOnConfigured((session) -> {
      if (cameraDevice != null) {
        cameraCaptureSession = session;
        updatePreview();
      }
    });

    captureListener = new CameraCaptureListener().setOnCompleted(() -> {
      Cropper rectangle = ImageProcessingService.getInstance().findCard(textureView.getBitmap());
      getActivity().runOnUiThread(() -> {
        processedView.setImageBitmap(rectangle.getOriginal());
      });
    });
    ImageProcessingService.getInstance().getCandidates().observe(this,(rects)-> {
      if (rects.size() > 10) {
//        ImageReviewFragment imageReviewFragment = new ImageReviewFragment();
//        getActivity().getSupportFragmentManager().beginTransaction()
//            .replace(R.id.fragment_container,
//                imageReviewFragment).commit();
        new Thread(()->{
          Bitmap bmp = ImageProcessingService.getInstance().getCroppedImage(textureView.getBitmap());
          FileManagerService.getInstance().saveImage(bmp);
          rects.clear();
        }).start();

      }
    });


  }

  private void updatePreview() {
    Log.d(TAG, "updating preview");
    if (cameraDevice != null) {
      captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
      try {
        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(),
            captureListener, mBackgroundHandler);
      } catch (CameraAccessException e) {
        e.printStackTrace();
      }
    }
  }

  private void createCameraPreview() {
    textureView.getSurfaceTexture()
        .setDefaultBufferSize(imageDimensions.getWidth(), imageDimensions.getHeight());
    surface = new Surface(textureView.getSurfaceTexture());
    try {
      captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
      captureRequestBuilder.addTarget(surface);

      cameraDevice.createCaptureSession(Arrays.asList(surface),
          sessionStateCallback, null);

    } catch (CameraAccessException e) {
      e.printStackTrace();
    }


  }

  private void openCamera() {
    Log.d(TAG, "opening camera");
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

  private void captureImage() {

    try {
      Log.d(TAG, "Capturing");
      reader = ImageReader.newInstance(imageDimensions.getWidth(),
          imageDimensions.getWidth(), ImageFormat.JPEG, 1);

      List<Surface> outputSurfaces = new ArrayList<>(2);

      outputSurfaces.add(reader.getSurface());

      Builder captureBuilder = cameraDevice
          .createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

      captureBuilder.addTarget(reader.getSurface());

      captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

      captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,
          characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION));

      CameraImageAvailableListener imageAvailableListener = new CameraImageAvailableListener()
          .setOnCompleted((imageReader) -> {
            Log.d(TAG, "Image available");
            try (Image image = reader.acquireLatestImage()) {
//
//              ByteBuffer buffer = image.getPlanes()[0].getBuffer();
//              byte[] bytes = new byte[buffer.capacity()];
//              buffer.get(bytes);
//              ByteBuffer bb = ByteBuffer.allocateDirect(textureView.getBitmap().getAllocationByteCount());
//              Log.d(TAG, "captureImage: "+height+ " : "+ width);
//              byte[] b= new byte[bb.capacity()];
//              textureView.getBitmap().
//              textureView.getBitmap().copyPixelsToBuffer(bb);
//              bb.put(b);


//              fileManagerService.saveImage(bytes, height, width);
            }
          });

      reader.setOnImageAvailableListener(imageAvailableListener, mBackgroundHandler);
      CameraCaptureListener captureListener = new CameraCaptureListener()
          .setOnCompleted(this::createCameraPreview);
      CameraCaptureSessionStateCallback stateCallback = new CameraCaptureSessionStateCallback()
          .setOnConfigured((session) -> {
            try {
              session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
              Log.d(TAG, "configuration successful");
            } catch (CameraAccessException e) {
              Log.e(TAG, "Configuration failed: " + e.getMessage());
            }
          });
      cameraDevice.createCaptureSession(outputSurfaces, stateCallback, mBackgroundHandler);
    } catch (CameraAccessException e) {
      Log.e(TAG, "failed to capture: " + e.getMessage());
    }
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
    Log.d(TAG, "starting background thread");
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
    closeCamera();
    super.onPause();
    Log.d(TAG, "onPause: ");
  }

  @Override
  public void onStop() {
    super.onStop();
    Log.d(TAG, "onStop: ");
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

  private void closeCamera() {
    if (null != cameraDevice) {
      cameraDevice.close();
    }

    if (null != reader) {
      reader.close();
    }
  }
}
