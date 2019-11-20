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

@Dao
public interface LastNameDao {


  @Insert
  Long insert(LastName name);


  @Query("SELECT * FROM lastname ORDER BY name ASC")
  LiveData<List<LastName>> getAll();


}
