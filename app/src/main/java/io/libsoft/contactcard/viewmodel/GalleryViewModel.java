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

public class GalleryViewModel extends AndroidViewModel {

  private ContactDatabase database;

  public GalleryViewModel(@NonNull Application application) {
    super(application);

    database = ContactDatabase.getInstance();
  }

  public LiveData<List<Image>> getImages() {
    return database.getImageDao().getAll();
  }
}
