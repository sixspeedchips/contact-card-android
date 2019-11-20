package io.libsoft.contactcard.controller;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest.permission;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import io.libsoft.contactcard.R;
import io.libsoft.contactcard.service.GoogleSignInService;

public class LoginActivity extends AppCompatActivity {

  private static final int LOGIN_REQUEST_CODE = 1000;
  private static final int PERMISSIONS_REQUEST_CODE = 1033;

  private GoogleSignInService service;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (ActivityCompat.checkSelfPermission(this, permission.CAMERA) == PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE)
        == PERMISSION_GRANTED) {
    } else {
      ActivityCompat.requestPermissions(this,
          new String[]{
              permission.CAMERA,
              permission.WRITE_EXTERNAL_STORAGE
          }, PERMISSIONS_REQUEST_CODE);
    }



    service = GoogleSignInService.getInstance();
    service.refresh()
        .addOnSuccessListener((account) -> {
          switchToMain();
        })
        .addOnFailureListener((ex) -> {
          setContentView(R.layout.activity_login);
          findViewById(R.id.sign_in).setOnClickListener((view) -> {
            service.startSignIn(this, LOGIN_REQUEST_CODE);
          });
        });


  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == LOGIN_REQUEST_CODE) {
      service.completeSignIn(data)
          .addOnSuccessListener((account) -> {
            switchToMain();
          })
          .addOnFailureListener((ex) -> {
            Toast.makeText(this, R.string.login_failed_toast, Toast.LENGTH_LONG)
                .show();
          });
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  private void switchToMain() {
    Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

}
