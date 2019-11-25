/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.model.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.libsoft.contactcard.model.entity.LastName;
import java.util.List;

/**
 * Dao for the Last name table.
 */
@Dao
public interface LastNameDao {

  /**
   * Inserts a last name into the table.
   *
   * @param name
   * @return {@link Long}
   */
  @Insert
  Long insert(LastName name);

  /**
   * Gets all the last names from the database.
   *
   * @return {@link LiveData<List<LastName>>}
   */
  @Query("SELECT * FROM lastname ORDER BY name ASC")
  LiveData<List<LastName>> getAll();


}
