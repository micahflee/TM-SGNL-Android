package org.tm.archive.sharing;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.tm.archive.R;
import org.tm.archive.components.AvatarImageView;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.util.MappingAdapter;
import org.tm.archive.util.MappingViewHolder;
import org.tm.archive.util.ViewUtil;
import org.tm.archive.util.viewholders.RecipientMappingModel;

public class ShareSelectionViewHolder extends MappingViewHolder<ShareSelectionMappingModel> {

  protected final @NonNull TextView name;

  public ShareSelectionViewHolder(@NonNull View itemView) {
    super(itemView);

    name = findViewById(R.id.recipient_view_name);
  }

  @Override
  public void bind(@NonNull ShareSelectionMappingModel model) {
    name.setText(model.getName(context));
  }

  public static @NonNull MappingAdapter.Factory<ShareSelectionMappingModel> createFactory(@LayoutRes int layout) {
    return new MappingAdapter.LayoutFactory<>(ShareSelectionViewHolder::new, layout);
  }
}
