/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.controller;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.squareup.picasso.Picasso;
import io.libsoft.contactcard.R;
import io.libsoft.contactcard.controller.listeners.OnSwipeTouchListener;
import io.libsoft.contactcard.service.FileManagerService;
import io.libsoft.contactcard.service.TextProcessorService;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {


  private static final String TAG = "ContactFragment";
  private Context context;
  private View view;
  private ImageView imageView;
  private EditText name;
  private EditText phone;
  private EditText email;

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_contact, container, false);
    context = inflater.getContext();

    initViews();
    initListeners();

    return view;
  }

  private void initViews() {
    imageView = view.findViewById(R.id.image_review);
    name = view.findViewById(R.id.parsed_name);
    phone = view.findViewById(R.id.parsed_phone);
    email = view.findViewById(R.id.parsed_email);
  }

  private void initListeners() {

    FileManagerService.getInstance().getFileData().observe(this, (file) -> {
      Log.d(TAG, "initListeners: " + file);
      Log.d(TAG, "initListeners: " + imageView);
      if (file != null) {
        Picasso.get().load(file).into(imageView);
      }
    });
    TextProcessorService.getInstance().getName().observe(this, name::setText);
    TextProcessorService.getInstance().getPhone().observe(this, phone::setText);
    TextProcessorService.getInstance().getEmail().observe(this, email::setText);
    view.setOnTouchListener(new OnSwipeTouchListener(context) {
      public void onSwipeRight() {

      }

      public void onSwipeLeft() {
        Log.d(TAG, "onSwipeLeft: ");
        TextProcessorService.getInstance().reset();
        FileManagerService.getInstance().reset();
        Navigation.findNavController(view)
            .navigate(R.id.action_contactFragment_to_cameraFragment);
      }


    });
  }

}
