package io.libsoft.model.entity;

import androidx.room.Entity;
import java.util.Date;

@Entity
public class Contact {


  private Long id;
  private Date dateCreated;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;


}
