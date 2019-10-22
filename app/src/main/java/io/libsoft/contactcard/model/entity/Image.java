package io.libsoft.contactcard.model.entity;

import androidx.annotation.NonNull;
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
        )
    }
)
public class Image {

  @ColumnInfo(name = "image_id")
  @PrimaryKey( autoGenerate = true )
  private Long id;


  @ColumnInfo(name = "contact_id", index = true)
  private Long contactId;

  @ColumnInfo(name = "image_url")
  private String Url;

  @ColumnInfo(name = "raw")
  private boolean raw;

  @NonNull
  @ColumnInfo(index = true)
  private Date created = new Date();

  public Long getId() {
    return id;
  }

  @NonNull
  public Date getCreated() {
    return created;
  }

  public Long getContactId() {
    return contactId;
  }

  public void setContactId(Long contactId) {
    this.contactId = contactId;
  }

  public String getUrl() {
    return Url;
  }

  public void setUrl(String url) {
    Url = url;
  }

  public boolean isRaw() {
    return raw;
  }

  public void setRaw(boolean raw) {
    this.raw = raw;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setCreated(@NonNull Date created) {
    this.created = created;
  }
}
