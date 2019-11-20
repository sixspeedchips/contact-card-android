/*
 * This work is Copyright, 2019, Isaac Lindland
 * All rights reserved.
 */

package io.libsoft.contactcard.service;

import android.app.Application;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import io.libsoft.contactcard.model.entity.Contact;
import io.libsoft.contactcard.model.entity.FirstName;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextProcessorService {


  private static final String TAG = "textprocessorservice";
  private static final String PHONE_EX = "((\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4})";
  private static final String EMAIL_EX = "([a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+)";
  private static Application applicationContext;
  private static Vibrator vibrator;
  private MutableLiveData<Contact> contactHolder;
  private MutableLiveData<String> name;
  private MutableLiveData<String> phone;
  private MutableLiveData<String> email;
  private ContactDatabase instance;
  private Matcher matcher;


  public TextProcessorService() {
    vibrator = (Vibrator) applicationContext.getSystemService(Context.VIBRATOR_SERVICE);
    contactHolder = new MutableLiveData<>();
    name = new MutableLiveData<>();
    phone = new MutableLiveData<>();
    email = new MutableLiveData<>();
  }

  public static void setApplicationContext(Application applicationContext) {
    TextProcessorService.applicationContext = applicationContext;
  }

  public static TextProcessorService getInstance() {
    return TextProcessorService.InstanceHolder.INSTANCE;
  }

  public void process(String rawText) {

    new Thread(() -> {
      Pattern phonePattern = Pattern.compile(PHONE_EX);
      Pattern emailPattern = Pattern.compile(EMAIL_EX);
      Log.d(TAG, "process: starting");
      String[] split = rawText.split("\\s+");
      Log.d(TAG, "process: split string: " + Arrays.toString(split));
      List<FirstName> container;

      for (int i = 0; i < split.length; i++) {
        instance = ContactDatabase.getInstance();
        container = instance.getFirstNameDao()
            .getNameContaining(split[i]);
        Log.d(TAG, "process: " + container);
        if (container != null && !container.isEmpty()) {
          Log.d(TAG, "process: found name " + container.get(0).getName());
          name.postValue(container.get(0).getName() + " " + split[i + 1].toLowerCase());
          break;
        }

      }

      matcher = phonePattern.matcher(rawText);
      if (matcher.find()) {
        Log.d(TAG, "process: found number");
        phone.postValue(matcher.group(0));

      } else {
        String p = rawText.replaceAll("[-.—]*", " ");
        matcher = phonePattern.matcher(p);
        if (matcher.find()) {
          Log.d(TAG, "process: found number");
          phone.postValue(matcher.group(0));
        }

      }
      matcher = emailPattern.matcher(rawText);
      if (matcher.find()) {
        Log.d(TAG, "process: email address");
        email.postValue(matcher.group(0));
        email.setValue("");
      }

    }).start();
  }

  public MutableLiveData<String> getName() {
    return name;
  }

  public MutableLiveData<String> getPhone() {
    return phone;
  }

  public MutableLiveData<String> getEmail() {
    return email;
  }

  private static class InstanceHolder {

    private static final TextProcessorService INSTANCE = new TextProcessorService();
  }


}
