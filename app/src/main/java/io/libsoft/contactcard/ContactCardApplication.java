package io.libsoft.contactcard;

import android.app.Application;
import android.util.Log;
import com.facebook.stetho.Stetho;
import com.squareup.picasso.Picasso;
import io.libsoft.contactcard.service.ContactDatabase;
import io.libsoft.contactcard.service.FileManagerService;
import io.libsoft.contactcard.service.GoogleSignInService;
import io.libsoft.contactcard.service.ImageProcessingService;
import io.libsoft.contactcard.service.TextProcessorService;
import org.opencv.android.OpenCVLoader;

public class ContactCardApplication extends Application {

  static {
    if (!OpenCVLoader.initDebug()) {
      Log.e("OpenCv", "Unable to load OpenCV");
    } else {
      Log.d("OpenCv", "OpenCV loaded");
    }
  }


  @Override
  public void onCreate() {
    super.onCreate();

    Stetho.initializeWithDefaults(this);
    GoogleSignInService.setApplicationContext(this);
    FileManagerService.setApplicationContext(this);
    ContactDatabase.setApplicationContext(this);
    ImageProcessingService.setApplicationContext(this);
    TextProcessorService.setApplicationContext(this);

    Picasso.setSingletonInstance(new Picasso.Builder(this).loggingEnabled(true).build());

    ContactDatabase database = ContactDatabase.getInstance();

    new Thread(() -> database.getContactDao().delete()).start();

  }


}
