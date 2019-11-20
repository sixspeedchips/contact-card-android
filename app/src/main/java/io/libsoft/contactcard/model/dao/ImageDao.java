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

/**
 * Dao for retrieving and saving image URLS
 */
@Dao
public interface ImageDao {

  /**
   * Inserts an image into the database.
   *
   * @param image
   * @return
   */
  @Insert
  Long insert(Image image);

  /**
   * Gets all the images wrapped with LiveData from the database.
   *
   * @return {@link LiveData<Image>}
   */
  @Query("SELECT * FROM image ORDER BY image_id ASC")
  LiveData<List<Image>> getAll();

  /**
   * Retrieves a LiveData wrapped image from the database corresponding to the passed in contact
   * Id.
   *
   * @param contactId
   * @return {@link LiveData<Image>}
   */
  @Query("SELECT * FROM image WHERE contact_id=:contactId ORDER BY raw ASC")
  LiveData<Image> getImagesByContactId(Long contactId);


}
