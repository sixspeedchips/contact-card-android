/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import io.libsoft.contactcard.model.entity.Image;
import io.libsoft.contactcard.service.ContactDatabase;
import java.util.List;

/**
 * Viewmodel for handling the retrieval of images from the database.
 */
public class GalleryViewModel extends AndroidViewModel {

  private ContactDatabase database;

  /**
   * Constructor
   * @param application which holds the view model.
   */
  public GalleryViewModel(@NonNull Application application) {
    super(application);

    database = ContactDatabase.getInstance();
  }

  /**
   * Livedata wrapper for the images pulled from the database.
   * @return {@link LiveData} of {@link Image} from the database.
   */
  public LiveData<List<Image>> getImages() {
    return database.getImageDao().getAll();
  }
}
