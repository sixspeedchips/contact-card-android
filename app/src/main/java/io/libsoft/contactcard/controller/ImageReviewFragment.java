package io.libsoft.contactcard.controller;


import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import io.libsoft.contactcard.R;
import io.libsoft.contactcard.controller.listeners.OnSwipeTouchListener;

public class ImageReviewFragment extends Fragment {


  private static final String TAG = "ImageReviewFragment";
  private Image image;
  private View view;
  private Context context;
  //  private ImageView imageView;
  private TextView imageView;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Log.d(TAG, "onCreateView: ");
    view = inflater.inflate(R.layout.fragment_image_review, container, false);
    context = inflater.getContext();
    imageView = view.findViewById(R.id.review_image);

    Log.d(TAG, "onCreateView: finished");

    imageView.setOnTouchListener(new OnSwipeTouchListener(context) {

      public void onSwipeRight() {
        Toast.makeText(context, "right", Toast.LENGTH_SHORT).show();

        Navigation.findNavController(view)
            .navigate(R.id.action_imageReviewFragment_to_contactFragment);

      }

      public void onSwipeLeft() {

        Toast.makeText(context, "left", Toast.LENGTH_SHORT).show();
      }


    });



    return view;


  }


}
