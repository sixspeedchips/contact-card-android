/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Database table corresponding to last names.
 */
@Entity
public class LastName {

  @ColumnInfo(name = "name_id")
  @PrimaryKey(autoGenerate = true)
  private Long id;

  @ColumnInfo(name = "name", index = true, collate = ColumnInfo.NOCASE)
  private String name;

  /**
   * Getter for the id.
   *
   * @return {@link Long}
   */
  public Long getId() {
    return id;
  }

  /**
   * Setterfor the Id.
   *
   * @param id
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Getter for the name.
   *
   * @return {@link String}
   */
  public String getName() {
    return name;
  }

  /**
   * Setter for the Last name.
   *
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }
}
