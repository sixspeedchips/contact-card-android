/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.model.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Table corresponding to a list of first name for quick lookup.
 */
@Entity
public class FirstName {


  @ColumnInfo(name = "id")
  @PrimaryKey(autoGenerate = true)
  private Long id;

  @ColumnInfo(name = "name", index = true, collate = ColumnInfo.NOCASE)
  private String name;

  /**
   * Gets the Id of each name.
   *
   * @return {@link Long}
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the Id of each name.
   *
   * @param id
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the name corresponding to the row.
   *
   * @return {@link String}
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the first name.
   *
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }
}
