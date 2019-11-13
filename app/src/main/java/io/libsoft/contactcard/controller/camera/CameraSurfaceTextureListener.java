package io.libsoft.contactcard.controller.camera;

import android.graphics.SurfaceTexture;
import android.view.TextureView.SurfaceTextureListener;

public class CameraSurfaceTextureListener implements SurfaceTextureListener {

  private OnComplete onComplete;
  private TextureUpdatedListener textureUpdatedListener;

  public CameraSurfaceTextureListener setOnComplete(OnComplete onComplete) {
    this.onComplete = onComplete;
    return this;
  }

  public CameraSurfaceTextureListener setTextureUpdatedListener(
      TextureUpdatedListener textureUpdatedListener) {
    this.textureUpdatedListener = textureUpdatedListener;
    return this;
  }

  @Override
  public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
    onComplete.complete();
  }

  @Override
  public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

  }

  @Override
  public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
    return false;
  }

  @Override
  public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    textureUpdatedListener.update(surface);
  }

  public interface TextureUpdatedListener {

    void update(SurfaceTexture surface);
  }

  public interface OnComplete {

    void complete();
  }
}
