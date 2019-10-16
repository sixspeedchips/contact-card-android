package io.libsoft.model.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(
    foreignKeys = {
        @ForeignKey(
            entity = Contact.class,
            childColumns = "contact_id",
            parentColumns = "contact_id",
            onDelete = ForeignKey.CASCADE
        )}
)
public class ProcessedText {

  @PrimaryKey
  @ColumnInfo(name = "processed_text_id")
  private Long id;

  @Nullable
  @ColumnInfo(name = "canonical_key", index = true)
  private CanonicalKey canonicalKey;


  @ColumnInfo(name = "contact_id")
  private Long contactId;

  @NonNull
  @ColumnInfo(index = true)
  private Date created = new Date();

  @NonNull
  @ColumnInfo(index = true)
  private Date updated  = new Date();

  @ColumnInfo(name = "field_key", index = true)
  private String key;

  @ColumnInfo(name = "field_value")
  private String value;


  public enum CanonicalKey{

    NULL,
    FIRSTNAME,
    LASTNAME,
    EMAIL,
    PHONENUMBER;

  }

}
