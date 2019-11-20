/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.controller;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import io.libsoft.contactcard.R;
import io.libsoft.contactcard.controller.listeners.OnSwipeTouchListener;
import io.libsoft.contactcard.service.ImageProcessingService;
import java.io.File;

public class ImageReviewFragment extends Fragment {


  private static final String TAG = "ImageReviewFragment";
  private Image image;
  private View view;
  private Context context;
  private ImageView imageView;
  private String file;


  @SuppressLint("ClickableViewAccessibility")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    Log.d(TAG, "onCreateView: ");
    view = inflater.inflate(R.layout.fragment_image_review, container, false);
    context = inflater.getContext();
    imageView = view.findViewById(R.id.review_image);

    if (getArguments() != null) {
      file = getArguments().getString("file");
      Bitmap bitmap = ImageProcessingService.getInstance().fileToBitmap(file);
      imageView.setImageBitmap(bitmap);
    }
    imageView.setOnTouchListener(new OnSwipeTouchListener(context) {

      public void onSwipeRight() {
        Navigation.findNavController(view)
            .navigate(R.id.action_imageReviewFragment_to_cameraFragment);
      }

      public void onSwipeLeft() {
        if (file != null) {
          Bundle extras = new Bundle();
          ImageProcessingService.getInstance().performOcr(new File(file));

          Navigation.findNavController(view)
              .navigate(R.id.action_imageReviewFragment_to_contactFragment, extras);
        }
      }


    });

    return view;


  }


}
