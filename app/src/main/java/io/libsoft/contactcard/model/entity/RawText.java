/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.util.Date;

/**
 * Room ORM for database corresponding to the rawtext output.
 */
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

  /**
   * Getter for the Id.
   *
   * @return {@link Long}
   */
  public Long getId() {
    return id;
  }

  /**
   * Setter for the Id
   *
   * @param id
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Getter for the date created.
   *
   * @return {@link Date}
   */
  @NonNull
  public Date getCreated() {
    return created;
  }

  /**
   * Sets the date created, auto-generated.
   *
   * @param created
   */
  public void setCreated(@NonNull Date created) {
    this.created = created;
  }

  /**
   * Getter for the contact Id {@link Contact}
   *
   * @return {@link Long}
   */
  public Long getContactId() {
    return contactId;
  }

  /**
   * Setter for the contact Id {@link Contact}
   *
   * @param contactId
   */
  public void setContactId(Long contactId) {
    this.contactId = contactId;
  }

  /**
   * @return
   */
  public String getTextOutput() {
    return textOutput;
  }

  public void setTextOutput(String textOutput) {
    this.textOutput = textOutput;
  }
}
