package org.tm.archive.contacts.avatars;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.makeramen.roundedimageview.RoundedDrawable;

import org.jetbrains.annotations.NotNull;
import org.tm.archive.R;
import org.tm.archive.conversation.colors.AvatarColor;

public class TransparentContactPhoto implements FallbackContactPhoto {

  public TransparentContactPhoto() {}

  @Override
  public Drawable asDrawable(@NonNull Context context, @NonNull AvatarColor color) {
    return asDrawable(context, color, false);
  }

  @Override
  public Drawable asDrawable(@NonNull Context context, @NonNull AvatarColor color, boolean inverted) {
    return RoundedDrawable.fromDrawable(context.getResources().getDrawable(android.R.color.transparent));
  }

  @Override
  public Drawable asSmallDrawable(@NonNull Context context, @NonNull AvatarColor color, boolean inverted) {
    return asDrawable(context, color, inverted);
  }

  @Override
  public Drawable asCallCard(@NonNull Context context) {
    return ContextCompat.getDrawable(context, R.drawable.ic_contact_picture_large);
  }

}
