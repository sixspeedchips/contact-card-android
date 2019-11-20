/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.libsoft.contactcard.model.entity.FirstName;
import java.util.List;

/**
 * Dao for preloaded first names
 */
@Dao
public interface FirstNameDao {

  /**
   * Inserts a first name into the database.
   *
   * @param name
   * @return {@link Long} id of the first name.
   */
  @Insert
  Long insert(FirstName name);

  /**
   * Get all first names in the database.
   * @return {@link LiveData<FirstName>}
   */
  @Query("SELECT * FROM firstname ORDER BY name ASC")
  LiveData<List<FirstName>> getAll();

  /**
   * Search string to look up a name in the database.
   * @param arg
   * @return {@link List<FirstName>}
   */
  @Query("SELECT * FROM firstname WHERE name=:arg")
  List<FirstName> getNameContaining(String arg);

}
