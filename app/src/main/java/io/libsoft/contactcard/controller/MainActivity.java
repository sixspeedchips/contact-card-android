package io.libsoft.contactcard.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import com.google.android.material.navigation.NavigationView;
import io.libsoft.contactcard.R;
import io.libsoft.contactcard.controller.camera.CameraFragment;
import io.libsoft.contactcard.service.FileManagerService;
import io.libsoft.contactcard.service.GoogleSignInService;
import io.libsoft.contactcard.service.ImageProcessingService;
import io.libsoft.contactcard.viewmodel.MainViewModel;
import org.opencv.android.BaseLoaderCallback;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener, NavHost {

  private static final String TAG = "MainActivity";
  private BaseLoaderCallback mLoaderCallback;
  private MainViewModel viewModel;
  private FileManagerService fileManagerService;
  private ImageProcessingService imageProcessingService;
  private CameraFragment cameraFragment;
  private ImageReviewFragment imageReviewFragment;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    fileManagerService = FileManagerService.getInstance();
    imageProcessingService = ImageProcessingService.getInstance();

    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    initViewModel();
    initListeners();

    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    ImageProcessingService.getInstance().getOcrResults().observe(this,(s)->{
      Log.d(TAG, "TEXT" + s);
      Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    });


  }

  private void initListeners() {
  }


  private void initViewModel() {
    viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    getLifecycle().addObserver(viewModel);
  }

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

  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.nav_camera) {
    } else if (id == R.id.nav_gallery) {
    } else if (id == R.id.nav_slideshow) {
    } else if (id == R.id.nav_manage) {
    } else if (id == R.id.nav_share) {

    } else if (id == R.id.nav_send) {

    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }


  private void signOut() {
    GoogleSignInService.getInstance().signOut()
        .addOnCompleteListener((task) -> {
          Intent intent = new Intent(this, LoginActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);
        });
  }


  @NonNull
  @Override
  public NavController getNavController() {
    return null;
  }

}
