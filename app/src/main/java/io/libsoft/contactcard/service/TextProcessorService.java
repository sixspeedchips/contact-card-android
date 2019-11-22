/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

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

/**
 * Text processing class. Names are checked against a database of names. Emails are parsed from the
 * rawtext via regex patterns. Phone numbers are parsed via regex patterns.
 */
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


  private TextProcessorService() {
    vibrator = (Vibrator) applicationContext.getSystemService(Context.VIBRATOR_SERVICE);
    contactHolder = new MutableLiveData<>();
    name = new MutableLiveData<>();
    phone = new MutableLiveData<>();
    email = new MutableLiveData<>();
  }

  /**
   * Set application context
   * @param applicationContext used for services in this class.
   */
  public static void setApplicationContext(Application applicationContext) {
    TextProcessorService.applicationContext = applicationContext;
  }

  /**
   * Singleton pattern getter.
   * @return the Singleton instance of this class.
   */
  public static TextProcessorService getInstance() {
    return TextProcessorService.InstanceHolder.INSTANCE;
  }

  /**
   * Processing function for raw text. Names are matched against a database
   * of names. Then emails and phones numbers are extracted using regex patterns,
   * the information is then posted to {@link MutableLiveData} which can be viewed.
   * @param rawText {@link String} to be parsed.
   */
  public void process(String rawText) {

    new Thread(() -> {
      name.postValue("");
      phone.postValue("");
      email.postValue("");

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
      }

    }).start();
  }

  /**
   * getter for the mutable live data of a processed name.
   * @return {@link MutableLiveData} of {@link String} containing a name.
   */
  public MutableLiveData<String> getName() {
    return name;
  }

  /**
   * getter for the mutable live data of a processed phone number.
   * @return {@link MutableLiveData} of {@link String} containing a phone number.
   */
  public MutableLiveData<String> getPhone() {
    return phone;
  }

  /**
   * getter for the mutable live data of a processed email.
   * @return {@link MutableLiveData} of {@link String} containing a email.
   */
  public MutableLiveData<String> getEmail() {
    return email;
  }

  private static class InstanceHolder {

    private static final TextProcessorService INSTANCE = new TextProcessorService();
  }


}
