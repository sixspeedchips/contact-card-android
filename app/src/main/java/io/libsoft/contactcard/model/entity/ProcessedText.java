package io.libsoft.contactcard.model.entity;

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


  @ColumnInfo(name = "contact_id", index = true)
  private Long contactId;

  @NonNull
  @ColumnInfo(index = true)
  private Date created = new Date();

  @ColumnInfo(name = "field_value")
  private String value;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Nullable
  public CanonicalKey getCanonicalKey() {
    return canonicalKey;
  }

  public void setCanonicalKey(@Nullable CanonicalKey canonicalKey) {
    this.canonicalKey = canonicalKey;
  }

  public Long getContactId() {
    return contactId;
  }

  public void setContactId(Long contactId) {
    this.contactId = contactId;
  }

  @NonNull
  public Date getCreated() {
    return created;
  }

  public void setCreated(@NonNull Date created) {
    this.created = created;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public enum CanonicalKey{
    NULL,
    FIRSTNAME,
    LASTNAME,
    EMAIL,
    PHONENUMBER
  }

}
