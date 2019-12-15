/*****************************************************
 * This work is Copyright, 2019, Isaac Lindland      *
 * All rights reserved.                              *
 *****************************************************/

package io.libsoft.contactcard.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import io.libsoft.contactcard.R;
import io.libsoft.contactcard.model.entity.Image;
import io.libsoft.contactcard.view.GalleryAdapter.Holder;
import java.io.File;
import java.util.List;

/**
 * Gallery adapter that allows viewing of database images.
 */
public class GalleryAdapter extends RecyclerView.Adapter<Holder> {


  private final Context context;
  private final List<Image> images;

  /**
   * Constructor for the adapter
   *
   * @param context for which events happen under.
   * @param images  to be displayed.
   */
  public GalleryAdapter(Context context, List<Image> images) {
    this.context = context;
    this.images = images;
  }

  /**
   * Creates and returns a {@link Holder} that can be bound to any {@link
   * Image} in this instance's list of items.
   *
   * @param parent enclosing {@link RecyclerView}.
   * @param viewType desired view type (ignored in this implementation).
   * @return {@link Holder} referencing inflated layout.
   */
  @NonNull
  @Override
  public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false);
    return new Holder(view);
  }

  /**
   * Binds the specified {@link Holder} to the {@link Image} at the specified position in this
   * adapter instance.
   *
   * @param holder {@link Holder} referencing a bindable {@link View}.
   * @param position index of item in the adapter's list to bind to {@code holder}.
   */
  @Override
  public void onBindViewHolder(@NonNull Holder holder, int position) {
    Image passphrase = images.get(position);
    holder.bind(position, passphrase);
  }

  /**
   * Returns the number of items in the this instance's list of passphrases.
   *
   * @return count.
   */
  @Override
  public int getItemCount() {
    return images.size();
  }

  /**
   * Function interface for a long press on a item in the gallery, currently unused.
   */
  @FunctionalInterface
  public interface OnContextListener {

    void onLongPress(Menu menu, int position, Image image);

  }

  /**
   * Binder for {@link View} items in a {@link RecyclerView} and {@link Image} items in a
   * {@link GalleryAdapter}.
   */
  public class Holder extends RecyclerView.ViewHolder {

    private final View view;

    private Holder(@NonNull View itemView) {
      super(itemView);
      view = itemView;
    }

    private void bind(int position, Image image) {
      Picasso.get().load(new File(image.getUrl())).into((ImageView) view.findViewById(R.id.gallery_image));
    }

  }
}

