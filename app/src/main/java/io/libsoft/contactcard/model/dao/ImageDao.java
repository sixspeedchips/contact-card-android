/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.libsoft.contactcard.model.entity.Image;
import java.util.List;

@Dao
public interface ImageDao {

  @Insert
  Long insert(Image image);

  @Query("SELECT * FROM image WHERE contact_id=:contactId ORDER BY raw ASC")
  LiveData<List<Image>> getImagesByContactId(Long contactId);

  @Query("SELECT * FROM image ORDER BY image_id ASC")
  LiveData<List<Image>> getAll();


}
