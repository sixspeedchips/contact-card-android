package io.libsoft.contactcard.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class LastName {

  @ColumnInfo(name = "name_id")
  @PrimaryKey(autoGenerate = true)
  private Long id;

  @ColumnInfo(name = "name", index = true, collate = ColumnInfo.NOCASE)
  private String name;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
