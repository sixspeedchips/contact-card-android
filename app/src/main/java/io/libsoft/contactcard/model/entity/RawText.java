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
public class RawText {

  @ColumnInfo(name = "raw_text_id")
  @PrimaryKey(autoGenerate = true)
  private Long id;

  @ColumnInfo(name = "contact_id", index = true)
  private Long contactId;

  @NonNull
  @ColumnInfo(name = "created", index = true)
  private Date created = new Date();

  @ColumnInfo(name = "text_output")
  private String textOutput;

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

  public void setId(Long id) {
    this.id = id;
  }

  public void setCreated(@NonNull Date created) {
    this.created = created;
  }

  public String getTextOutput() {
    return textOutput;
  }

  public void setTextOutput(String textOutput) {
    this.textOutput = textOutput;
  }
}
