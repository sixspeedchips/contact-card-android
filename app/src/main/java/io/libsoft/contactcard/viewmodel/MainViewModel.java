/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import io.libsoft.contactcard.service.ImageProcessingService;
import io.reactivex.disposables.CompositeDisposable;
import java.io.File;

/**
 * Mainviewmodel is currently not being used.
 */
public class MainViewModel extends AndroidViewModel implements LifecycleObserver {
  //TODO clean this class up, removing extraneous references.

  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;
  private final ImageProcessingService processingService;
  private final MutableLiveData<GoogleSignInAccount> account;

  private final MutableLiveData<File> image;

  public MainViewModel(@NonNull Application application) {
    super(application);
    pending = new CompositeDisposable();

    processingService = ImageProcessingService.getInstance();
    throwable = new MutableLiveData<>();
    image = new MutableLiveData<>();
    account = new MutableLiveData<>();


  }




  /**
   * Returns the most recently thrown exception or error.
   */
  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  /**
   * Sets the currently logged-in user.
   */
  public void setAccount(GoogleSignInAccount account) {
    this.account.setValue(account);
  }


  @OnLifecycleEvent(Event.ON_STOP)
  private void clearPending() {
    pending.clear();
  }

}