package io.libsoft.model.entity;

import androidx.room.Entity;
import java.util.Date;

@Entity
public class RawText {


  private Long id;
  private Long contactId;
  private Date dateCreated;
  private String textOutput;

}
