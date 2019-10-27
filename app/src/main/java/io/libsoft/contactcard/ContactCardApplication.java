package io.libsoft.contactcard;

import android.app.Application;
import com.facebook.stetho.Stetho;
import com.squareup.picasso.Picasso;
import io.libsoft.contactcard.service.ContactDatabase;
import io.libsoft.contactcard.service.GoogleSignInService;

public class ContactCardApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);
    GoogleSignInService.setApplicationContext(this);
    Picasso.setSingletonInstance(new Picasso.Builder(this).loggingEnabled(true).build());
    ContactDatabase.setApplicationContext(this);
    ContactDatabase database = ContactDatabase.getInstance();
    new Thread(() -> database.getContactDao().delete()).start();

  }
}
