package io.libsoft.model.entity;

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

  @ColumnInfo( name = "processes_image_id" )
  @PrimaryKey( autoGenerate = true )
  private Long id;

  @ColumnInfo( name = "contact_id" )
  private Long contactId;

  @ColumnInfo(name = "image_url")
  private String Url;

  @ColumnInfo(name = "raw")
  private boolean isRaw;

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
    return isRaw;
  }

  public void setRaw(boolean raw) {
    isRaw = raw;
  }
}
