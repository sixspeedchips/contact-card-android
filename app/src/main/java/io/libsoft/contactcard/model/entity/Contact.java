/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.model.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

/**
 * Entity class for the contact built from parsing an business card.
 */
@Entity
public class Contact {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "contact_id")
  private Long id;


  @Nullable
  @ColumnInfo(name = "first_name", index = true)
  private String firstName;

  @Nullable
  @ColumnInfo(name = "last_name", index = true)
  private String lastName;

  @Nullable
  @ColumnInfo
  private String email;

  @Nullable
  @ColumnInfo
  private String phone;

  @NonNull
  @ColumnInfo(index = true)
  private Date created = new Date();

  @NonNull
  @ColumnInfo(index = true)
  private Date updated = new Date();


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /**
   * get first name of the contact.
   *
   * @return {@link String}.
   */
  @Nullable
  public String getFirstName() {
    return firstName;
  }

  /**
   * Set first name of the contact.
   *
   * @param firstName
   */
  public void setFirstName(@Nullable String firstName) {
    this.firstName = firstName;
  }

  /**
   * Get last name of the contact.
   *
   * @return {@link String} last name.
   */
  @Nullable
  public String getLastName() {
    return lastName;
  }

  /**
   * Sets the last naem of the contact.
   *
   * @param lastName
   */
  public void setLastName(@Nullable String lastName) {
    this.lastName = lastName;
  }

  /**
   * Get email for the contact.
   * @return {@link String}.
   */
  @Nullable
  public String getEmail() {
    return email;
  }

  /**
   * Set email for the contact.
   *
   * @param email
   */
  public void setEmail(@Nullable String email) {
    this.email = email;
  }

  /**
   * Get the phone of the contact.
   * @return {@link String}
   */
  @Nullable
  public String getPhone() {
    return phone;
  }

  /**
   * Set the phone number of the contact.
   *
   * @param phone
   */
  public void setPhone(@Nullable String phone) {
    this.phone = phone;
  }

  /**
   * Gets the date created of the contact.
   *
   * @return {@link Date}
   */
  @NonNull
  public Date getCreated() {
    return created;
  }

  /**
   * Auto-generated date of the contact creation.
   *
   * @param created
   */
  public void setCreated(@NonNull Date created) {
    this.created = created;
  }

  /**
   * Get the last updated time of the contact.
   * @return {@link Date}
   */
  @NonNull
  public Date getUpdated() {
    return updated;
  }

  /**
   * Set the date updated of the contact.
   * @param updated
   */
  public void setUpdated(@NonNull Date updated) {
    this.updated = updated;
  }
}
