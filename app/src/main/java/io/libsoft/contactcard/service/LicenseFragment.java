package io.libsoft.contactcard.service;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import io.libsoft.contactcard.R;

/**
 * A simple {@link Fragment} subclass that displays licenses.
 */
public class LicenseFragment extends Fragment {


  private View view;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_license, container, false);
    return view;
  }


}
