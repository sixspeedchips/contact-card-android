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
    contactHolder = new MutableLiveData<>();
    name = new MutableLiveData<>();
    phone = new MutableLiveData<>();
    email = new MutableLiveData<>();
    instance = ContactDatabase.getInstance();
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

      Pattern phonePattern = Pattern.compile(PHONE_EX);
      Pattern emailPattern = Pattern.compile(EMAIL_EX);
      Log.d(TAG, "process: starting");
      String[] split = rawText.toLowerCase().trim().split("\\s+");
      Log.d(TAG, "process: split length " + split.length);
      Log.d(TAG, "process: split string: " + Arrays.toString(split));
      FirstName firstName;

      for (int i = 0; i < split.length; i++) {
        Log.d(TAG, "process: checking " + split[i]);
        firstName = instance.getFirstNameDao().getNameContaining(split[i]);
        Log.d(TAG, "process: " + firstName);
        if (firstName != null) {
          Log.d(TAG, "process: found name " + firstName.getName());
          name.postValue(firstName.getName() + " " + split[i + 1].toLowerCase());
          break;
        }

      }


        String p = rawText.replaceAll("[-.—_]+", " ");
        matcher = phonePattern.matcher(p);
        if (matcher.find()) {
          Log.d(TAG, "process: found number");
          phone.postValue(matcher.group(0));
        }

      matcher = emailPattern.matcher(rawText);
      if (matcher.find()) {
        Log.d(TAG, "process: email address");
        email.postValue(matcher.group(0));
      }
      ((Vibrator) applicationContext.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(200);

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

  public void reset() {
    name.setValue("");
    email.setValue("");
    phone.setValue("");
  }

  private static class InstanceHolder {

    private static final TextProcessorService INSTANCE = new TextProcessorService();
  }


}
