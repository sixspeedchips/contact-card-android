package io.libsoft.contactcard.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.libsoft.contactcard.model.entity.FirstName;
import java.util.List;

@Dao
public interface FirstNameDao {

  @Insert
  Long insert(FirstName name);

  @Query("SELECT * FROM firstname ORDER BY name ASC")
  LiveData<List<FirstName>> getAll();

  @Query("SELECT * FROM firstname WHERE name=:arg")
  List<FirstName> getNameContaining(String arg);

}
