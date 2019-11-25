/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.libsoft.contactcard.model.entity.ProcessedText;

/**
 * Dao for the table corresponding to the processed Text
 */
@Dao
public interface ProcessedTextDao {

  /**
   * Inserts a result of processedtext.
   *
   * @param processedText
   * @return {@link Long}
   */
  @Insert
  Long insert(ProcessedText processedText);

  /**
   * Returns a List of Processed texts corresponding to a contact
   *
   * @param contactId
   * @return {@link LiveData<ProcessedText>}
   */
  @Query("SELECT * FROM processedtext WHERE contact_id=:contactId ORDER BY canonical_key ASC")
  LiveData<ProcessedText> getTextsById(Long contactId);


}
