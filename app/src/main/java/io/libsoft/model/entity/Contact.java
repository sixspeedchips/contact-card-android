package io.libsoft.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity
public class Contact {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "contact_id")
  private Long id;

  @NonNull
  @ColumnInfo(index = true)
  private Date dateCreated = new Date();

  @NonNull
  @ColumnInfo(index = true)
  private Date dateUpdated = new Date();

  @ColumnInfo(index = true)
  private String firstName;

  @ColumnInfo(index = true)
  private String lastName;

  @ColumnInfo
  private String email;

  @ColumnInfo
  private String phone;

  public Long getId() {
    return id;
  }

  @NonNull
  public Date getDateCreated() {
    return dateCreated;
  }

  @NonNull
  public Date getDateUpdated() {
    return dateUpdated;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }
}
