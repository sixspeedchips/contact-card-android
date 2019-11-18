package io.libsoft.contactcard.controller;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import io.libsoft.contactcard.R;
import io.libsoft.contactcard.controller.listeners.OnSwipeTouchListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {


  private Context context;
  private View view;

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    view = inflater.inflate(R.layout.fragment_contact, container, false);
    context = inflater.getContext();
    TextView textView = view.findViewById(R.id.contact_view);
    textView.setOnTouchListener(new OnSwipeTouchListener(context) {

      public void onSwipeRight() {
        Toast.makeText(context, "right", Toast.LENGTH_SHORT).show();
      }

      public void onSwipeLeft() {
        Toast.makeText(context, "left", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view)
            .navigate(R.id.action_contactFragment_to_imageReviewFragment);

      }


    });

    return view;
  }

}
