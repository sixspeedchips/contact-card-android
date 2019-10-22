package io.libsoft.contactcard.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import io.libsoft.contactcard.model.entity.Contact;

@Dao
public interface ContactDao {

  @Insert
  Long insert(Contact contact);

  @Query("SELECT * FROM contact WHERE contact_id=:id")
  Contact getContactById(Long id);

  @Update
  int update(Contact contact);

  @Delete
  int delete(Contact... contacts);


}
