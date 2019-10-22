package io.libsoft.contactcard;

import android.app.Application;
import com.facebook.stetho.Stetho;
import com.squareup.picasso.Picasso;
import io.libsoft.contactcard.service.ContactDatabase;

public class ContactCardApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);
    Picasso.setSingletonInstance(new Picasso.Builder(this).loggingEnabled(true).build());
    ContactDatabase.setApplicationContext(this);
    final ContactDatabase database = ContactDatabase.getInstance();
    new Thread(() -> database.getContactDao().delete()).start();

  }
}
