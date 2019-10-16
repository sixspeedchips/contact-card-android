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
public class RawText {

  @ColumnInfo(name = "raw_text_id")
  @PrimaryKey(autoGenerate = true)
  private Long id;

  @ColumnInfo(name = "contact_id")
  private Long contactId;

  @NonNull
  @ColumnInfo(index = true)
  private Date dateCreated = new Date();

  @ColumnInfo
  private String textOutput;

  public Long getId() {
    return id;
  }

  @NonNull
  public Date getDateCreated() {
    return dateCreated;
  }

  public Long getContactId() {
    return contactId;
  }

  public void setContactId(Long contactId) {
    this.contactId = contactId;
  }

  public String getTextOutput() {
    return textOutput;
  }

  public void setTextOutput(String textOutput) {
    this.textOutput = textOutput;
  }
}
