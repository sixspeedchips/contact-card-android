/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

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


/**
 * Room ORM
 */
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

  /**
   * Sets the application context
   *
   * @param application
   */
  public static void setApplicationContext(Application application) {
    ContactDatabase.applicationContext = application;
  }

  /**
   * Singleton pattern, gets the instance of the class.
   *
   * @return {@link ContactDatabase}
   */
  public static ContactDatabase getInstance() {
    return InstanceHolder.INSTANCE;
  }

  /**
   * Annotations for the Dao for the contacts.
   *
   * @return {@link ContactDao}
   */
  public abstract ContactDao getContactDao();

  /**
   * Annotations for the Dao for the Processed Text.
   *
   * @return {@link ProcessedTextDao}
   */
  public abstract ProcessedTextDao getProcessedTextDao();

  /**
   * Annotations for the Dao for the Image.
   *
   * @return {@link ImageDao}
   */
  public abstract ImageDao getImageDao();

  /**
   * Annotations for the Dao for the Raw Text.
   *
   * @return {@link RawTextDao}
   */
  public abstract RawTextDao getRawTextDao();

  /**
   * Annotations for the Dao for the FirstName.
   *
   * @return {@link FirstNameDao}
   */
  public abstract FirstNameDao getFirstNameDao();

  /**
   * Annotations for the Dao for the LastName
   *
   * @return {@link LastNameDao}
   */
  public abstract LastNameDao getLastNameDao();

  private static class InstanceHolder {

    private static final ContactDatabase INSTANCE;

    static {
      INSTANCE =
          Room.databaseBuilder(applicationContext, ContactDatabase.class, "contact_db")
              .addCallback(new Callback() {

                /**
                 * On database created load in first and last names into tables for further
                 * queries.
                 *
                 * @param db
                 */
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                  Log.d(TAG, "onCreate: STARTING");
                  super.onCreate(db);
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

  /**
   * Converters for Room ORM
   */
  public static class Converters {

    /**
     * Converts {@link Date} objects to {@link long}
     *
     * @param date
     * @return {@link long} representing the date.
     */
    @TypeConverter
    public Long dateToLong(Date date) {
      return (date != null) ? date.getTime() : null;
    }

    /**
     * Converts {@link long} objects to {@link Date}
     *
     * @param milliseconds
     * @return {@link Date}
     */
    @TypeConverter
    public Date longToDate(Long milliseconds) {
      return (milliseconds != null) ? new Date(milliseconds) : null;
    }

    /**
     * Converts an enum to its string representation.
     *
     * @param value
     * @return {@link String}
     */
    @TypeConverter
    public String enumToString(Enum value) {
      return (value != null) ? value.toString() : null;
    }

    /**
     * Returns a enum representing a contact fields from a string
     *
     * @param name
     * @return {@link CanonicalKey}
     */
    @TypeConverter
    public CanonicalKey stringToCanonicalKey(String name) {
      return (name != null) ? CanonicalKey.valueOf(name) : null;
    }
  }
}
