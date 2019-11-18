package io.libsoft.contactcard.controller;


import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import io.libsoft.contactcard.R;

public class ImageReviewFragment extends Fragment {


  private Image image;
  private View view;
  private Context context;
  private ImageView imageView;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_image_review, container, false);
    context = inflater.getContext();
    imageView = view.findViewById(R.id.review_image);
    return view;
  }

  public void setImage(Image setImage) {
//    this.setImage = setImage;
//    imageView.setImageBitmap();

  }
}
