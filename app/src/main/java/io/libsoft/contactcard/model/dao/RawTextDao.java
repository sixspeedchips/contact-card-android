/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.libsoft.contactcard.model.entity.RawText;

/**
 * Dao the for raw text.
 */
@Dao
public interface RawTextDao {

  /**
   * Inserts a raw text result into the database.
   *
   * @param rawText
   * @return {@link Long} id of the inserted text.
   */
  @Insert
  Long insert(RawText rawText);

  /**
   * Gets the raw text corresponding to a contact.
   * @param contactId
   * @return {@link RawText}
   */
  @Query("SELECT * FROM rawtext WHERE contact_id=:contactId")
  RawText getTextByContactId(Long contactId);

}
