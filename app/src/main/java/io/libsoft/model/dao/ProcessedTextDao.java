package io.libsoft.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.libsoft.model.entity.ProcessedText;
import java.util.List;

@Dao
public interface ProcessedTextDao {

  @Insert
  Long insert(ProcessedText processedText);

  @Query("SELECT * FROM processedtext WHERE contact_id=:contactId ORDER BY canonical_key ASC")
  List<ProcessedText> getTextsById(Long contactId);


}
