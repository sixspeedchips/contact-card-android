package io.libsoft.contactcard.service;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import io.libsoft.contactcard.model.dao.ContactDao;
import io.libsoft.contactcard.model.dao.FirstNameDao;
import io.libsoft.contactcard.model.dao.ImageDao;
import io.libsoft.contactcard.model.dao.LastNameDao;
import io.libsoft.contactcard.model.dao.ProcessedTextDao;
import io.libsoft.contactcard.model.dao.RawTextDao;
import io.libsoft.contactcard.model.entity.Contact;
import io.libsoft.contactcard.model.entity.FirstName;
import io.libsoft.contactcard.model.entity.Image;
import io.libsoft.contactcard.model.entity.LastName;
import io.libsoft.contactcard.model.entity.ProcessedText;
import io.libsoft.contactcard.model.entity.ProcessedText.CanonicalKey;
import io.libsoft.contactcard.model.entity.RawText;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Database(
    entities = {Contact.class, Image.class,
        ProcessedText.class, RawText.class,
        FirstName.class, LastName.class},
    version = 1, exportSchema = true
)
@TypeConverters(ContactDatabase.Converters.class)
public abstract class ContactDatabase extends RoomDatabase {

  private static final String TAG = "DATABASE";
  private static Application applicationContext;

  protected ContactDatabase() {
  }

  public static void setApplicationContext(Application application) {
    ContactDatabase.applicationContext = application;
  }

  public static ContactDatabase getInstance() {
    return InstanceHolder.INSTANCE;
  }


  public abstract ContactDao getContactDao();

  public abstract ProcessedTextDao getProcessedTextDao();

  public abstract ImageDao getImageDao();

  public abstract RawTextDao getRawTextDao();

  public abstract FirstNameDao getFirstNameDao();

  public abstract LastNameDao getLastNameDao();

  private static class InstanceHolder {

    private static final ContactDatabase INSTANCE;

    static {
      INSTANCE =
          Room.databaseBuilder(applicationContext, ContactDatabase.class, "contact_db")
              .addCallback(new Callback() {

                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                  Log.d(TAG, "onCreate: STARTING");
                  super.onCreate(db);
                  Log.d(TAG, "onCreate: after super");
                  new Thread(() -> {

                    ContactDatabase database = ContactDatabase.getInstance();
                    Log.d(TAG, "onCreate: getting names from file");
                    Map<String, List<String>> namesMap = FileManagerService.getInstance()
                        .getNames();
                    Log.d(TAG, "onCreate: map created");
                    Log.d(TAG, "onCreate: saving first names to db");
                    for (String first : namesMap.get("first")) {
                      FirstName firstName = new FirstName();
                      firstName.setName(first);
                      database.getFirstNameDao().insert(firstName);
                    }
                    Log.d(TAG, "onCreate: finished");
                    Log.d(TAG, "onCreate: saving last names to db");
                    for (String last : namesMap.get("last")) {
                      LastName lastName = new LastName();
                      lastName.setName(last);
                      database.getLastNameDao().insert(lastName);
                    }
                    Log.d(TAG, "onCreate: finished");
                  }).start();
                  Log.d(TAG, "onCreate: after thread");
                }
              })
              .build();
    }
  }

  public static class Converters {

    @TypeConverter
    public Long dateToLong(Date date) {
      return (date != null) ? date.getTime() : null;
    }

    @TypeConverter
    public Date longToDate(Long milliseconds) {
      return (milliseconds != null) ? new Date(milliseconds) : null;
    }

    @TypeConverter
    public String enumToString(Enum value) {
      return (value != null) ? value.toString() : null;
    }

    @TypeConverter
    public CanonicalKey stringToCanonicalKey(String name) {
      return (name != null) ? CanonicalKey.valueOf(name) : null;
    }
  }
}
