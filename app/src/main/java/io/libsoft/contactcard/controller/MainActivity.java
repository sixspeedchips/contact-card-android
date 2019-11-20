/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import io.libsoft.contactcard.R;
import io.libsoft.contactcard.controller.camera.CameraFragment;
import io.libsoft.contactcard.service.FileManagerService;
import io.libsoft.contactcard.service.GoogleSignInService;
import io.libsoft.contactcard.service.ImageProcessingService;
import io.libsoft.contactcard.viewmodel.MainViewModel;
import org.opencv.android.BaseLoaderCallback;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

  private static final String TAG = "MainActivity";
  private final int REQUEST_CODE = 1033;
  private BaseLoaderCallback mLoaderCallback;
  private MainViewModel viewModel;
  private FileManagerService fileManagerService;
  private ImageProcessingService imageProcessingService;
  private CameraFragment cameraFragment;
  private ImageReviewFragment imageReviewFragment;
  private View view;


  /**
   * On create for main activity
   *
   * @param savedInstanceState takes the saved instance state to be loaded.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    fileManagerService = FileManagerService.getInstance();
    imageProcessingService = ImageProcessingService.getInstance();

    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    view = findViewById(R.id.container_fragment);
    setSupportActionBar(toolbar);
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    navigationView.bringToFront();

  }

  /**
   * Specifies behaviour for back button in drawer.
   */
  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.options, menu);
    return true;
  }

  /**
   * Navigation within the options menu.
   * @param item clicked on.
   * @return boolean behaviour handled.
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    } else if (id == R.id.sign_out) {
      signOut();
    }

    return super.onOptionsItemSelected(item);
  }


  private void signOut() {
    GoogleSignInService.getInstance().signOut()
        .addOnCompleteListener((task) -> {
          Intent intent = new Intent(this, LoginActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);
        });
  }

  /**
   * Navigation within the main activity, switched between the camera and the gallery
   * @param menuItem that was clicked on
   * @return boolean the handled state.
   */
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
    Log.d(TAG, "onNavigationItemSelected: ");
    int id = menuItem.getItemId();
    if (id == R.id.nav_camera) {
      Log.d(TAG, "onNavigationItemSelected: camera selected");
      Navigation.findNavController(this, R.id.container_fragment).navigate(R.id.nav_camera);

    } else if (id == R.id.nav_gallery) {
      Log.d(TAG, "onNavigationItemSelected: gallery selected");
      Navigation.findNavController(view).navigate(R.id.nav_gallery);
    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
}
