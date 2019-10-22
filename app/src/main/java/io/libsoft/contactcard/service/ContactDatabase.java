package io.libsoft.contactcard.service;

import android.app.Application;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import io.libsoft.contactcard.model.dao.ContactDao;
import io.libsoft.contactcard.model.dao.ImageDao;
import io.libsoft.contactcard.model.dao.ProcessedTextDao;
import io.libsoft.contactcard.model.dao.RawTextDao;
import io.libsoft.contactcard.model.entity.Contact;
import io.libsoft.contactcard.model.entity.Image;
import io.libsoft.contactcard.model.entity.ProcessedText;
import io.libsoft.contactcard.model.entity.ProcessedText.CanonicalKey;
import io.libsoft.contactcard.model.entity.RawText;
import java.util.Date;


@Database(
    entities = {Contact.class, Image.class, ProcessedText.class, RawText.class},
    version = 1, exportSchema = true
)
@TypeConverters(ContactDatabase.Converters.class)
public abstract class ContactDatabase extends RoomDatabase {

  private static Application applicationContext;

  ;

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


  private static class InstanceHolder {

    private static final ContactDatabase INSTANCE;

    static {
      INSTANCE =
          Room.databaseBuilder(applicationContext, ContactDatabase.class, "contact_db").build();
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
