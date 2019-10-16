package io.libsoft.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import io.libsoft.model.entity.Image;
import java.util.List;

@Dao
public interface ImageDao {

  @Insert
  Long insert(Image image);

}
