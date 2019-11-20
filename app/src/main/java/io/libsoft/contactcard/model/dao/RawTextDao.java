/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.libsoft.contactcard.model.entity.RawText;

@Dao
public interface RawTextDao {


  @Insert
  Long insert(RawText rawText);

  @Query("SELECT * FROM rawtext WHERE contact_id=:contactId")
  RawText getTextByContactId(Long contactId);

}
