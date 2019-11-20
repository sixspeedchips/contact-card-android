package io.libsoft.contactcard.controller;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import io.libsoft.contactcard.R;
import io.libsoft.contactcard.controller.listeners.OnSwipeTouchListener;
import io.libsoft.contactcard.service.ImageProcessingService;
import io.libsoft.contactcard.service.TextProcessorService;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {


  private static final String TAG = "ContactFragment";
  private Context context;
  private View view;

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_contact, container, false);
    context = inflater.getContext();
    TextView textView = view.findViewById(R.id.raw_text);
    EditText name = view.findViewById(R.id.parsed_name);
    EditText phone = view.findViewById(R.id.parsed_phone);
    EditText email = view.findViewById(R.id.parsed_email);
    ImageProcessingService.getInstance().getOcrResults().observe(this, textView::setText);
    TextProcessorService.getInstance().getName().observe(this, name::setText);
    TextProcessorService.getInstance().getPhone().observe(this, phone::setText);
    TextProcessorService.getInstance().getEmail().observe(this, email::setText);
    textView.setOnTouchListener(new OnSwipeTouchListener(context) {
      public void onSwipeRight() {

      }

      public void onSwipeLeft() {
        Log.d(TAG, "onSwipeLeft: ");

        phone.setText("");
        email.setText("");
        name.setText("");
        Navigation.findNavController(view)
            .navigate(R.id.action_contactFragment_to_cameraFragment);
      }


    });

    return view;
  }

}
