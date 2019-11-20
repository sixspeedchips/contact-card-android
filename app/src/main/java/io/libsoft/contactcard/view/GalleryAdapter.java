/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.libsoft.contactcard.R;
import io.libsoft.contactcard.model.entity.Image;
import io.libsoft.contactcard.service.ImageProcessingService;
import io.libsoft.contactcard.view.GalleryAdapter.Holder;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<Holder> {


  private final Context context;
  private final List<Image> images;
//  private final OnClickListener clickListener;
//  private final OnContextListener contextListener;


  public GalleryAdapter(Context context, List<Image> images) {
    this.context = context;
    this.images = images;
//    this.clickListener = clickListener;
//    this.contextListener = contextListener;
  }


  @NonNull
  @Override
  public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false);
    return new Holder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull Holder holder, int position) {
    Image passphrase = images.get(position);
    holder.bind(position, passphrase);
  }

  @Override
  public int getItemCount() {
    return images.size();
  }

  @FunctionalInterface
  public interface OnContextListener {

    void onLongPress(Menu menu, int position, Image image);

  }

  public class Holder extends RecyclerView.ViewHolder {

    private final View view;

    private Holder(@NonNull View itemView) {
      super(itemView);
      view = itemView;
    }

    private void bind(int position, Image image) {

      Bitmap bmp = ImageProcessingService.getInstance().fileToBitmap(image.getUrl());
      ((ImageView) view.findViewById(R.id.gallery_image)).setImageBitmap(bmp);
    }

  }
}

