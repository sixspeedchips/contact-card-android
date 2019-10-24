package io.libsoft.contactcard.model.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

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

  @NonNull
  public Date getCreated() {
    return created;
  }

    public void setCreated(@NonNull Date created) {
    this.created = created;
  }

  public void setDateUpdated() {
    this.updated = new Date();
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

  @NonNull
  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(@NonNull Date updated) {
    this.updated = updated;
  }

  public void setId(Long id) {
    this.id = id;
  }

}
