package io.libsoft.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.libsoft.model.entity.RawText;

@Dao
public interface RawTextDao {


  @Insert
  Long insert(RawText rawText);

  @Query("SELECT * FROM rawtext WHERE contact_id=:contactId")
  RawText getTextByContactId(Long contactId);

}
