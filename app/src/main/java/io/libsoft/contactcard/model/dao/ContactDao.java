/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import io.libsoft.contactcard.model.entity.Contact;
import java.util.List;

/**
 * Dao for the contact data
 */
@Dao
public interface ContactDao {

  /**
   * Inserts a contact in the Database.
   *
   * @param contact to be inserted.
   * @return the id of the contact inserted.
   */
  @Insert
  Long insert(Contact contact);

  /**
   * Get a contact from the Database by the id.
   * @param id to be queried.
   * @return a {@link Contact} matching the id.
   */
  @Query("SELECT * FROM contact WHERE contact_id=:id")
  Contact getContactById(Long id);

  /**
   * Retrieves all the contacts from the Database.
   * @return {@link List} of contacts.
   */
  @Query("SELECT * FROM contact")
  List<Contact> getAllContacts();

  /**
   * Updates a contact in the Database.
   * @param contact to be updated.
   * @return id of the contact updated.
   */
  @Update
  int update(Contact contact);

  /**
   * Deletes a set of contacts from the database.
   * @param contacts to be deleted
   * @return
   */
  @Delete
  int delete(Contact... contacts);


}
