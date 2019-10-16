package io.libsoft.model.entity;

import androidx.room.Entity;
import java.util.Date;
import java.util.List;

@Entity
public class ProcessedText {

  private Long id;
  private Long contactId;
  private Long rawTextId;
  private Date dateCreated;
  private List<String> processedText;

}
