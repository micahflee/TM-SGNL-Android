package org.tm.archive.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.tm.archive.R;
import org.tm.archive.components.emoji.SimpleEmojiTextView;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.util.ContextUtil;
import org.tm.archive.util.DrawableUtil;
import org.tm.archive.util.SpanUtil;
import org.tm.archive.util.ViewUtil;

public class FromTextView extends SimpleEmojiTextView {

  public FromTextView(Context context) {
    super(context);
  }

  public FromTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setText(Recipient recipient) {
    setText(recipient, null);
  }

  public void setText(Recipient recipient, @Nullable CharSequence suffix) {
    setText(recipient, recipient.getDisplayName(getContext()), suffix);
  }

  public void setText(Recipient recipient, @Nullable CharSequence fromString, @Nullable CharSequence suffix) {
    setText(recipient, fromString, suffix, true);
  }

  public void setText(Recipient recipient, @Nullable CharSequence fromString, @Nullable CharSequence suffix, boolean asThread) {
    SpannableStringBuilder builder  = new SpannableStringBuilder();

    if (asThread && recipient.isSelf()) {
      builder.append(getContext().getString(R.string.note_to_self));
    } else {
      builder.append(fromString);
    }

    if (suffix != null) {
      builder.append(suffix);
    }

    if (asThread && recipient.showVerified()) {
      Drawable official = ContextUtil.requireDrawable(getContext(), R.drawable.ic_official_20);
      official.setBounds(0, 0, ViewUtil.dpToPx(20), ViewUtil.dpToPx(20));

      builder.append(" ")
             .append(SpanUtil.buildCenteredImageSpan(official));
    }

    setText(builder);

    if      (recipient.isBlocked()) setCompoundDrawablesRelativeWithIntrinsicBounds(getBlocked(), null, null, null);
    else if (recipient.isMuted())   setCompoundDrawablesRelativeWithIntrinsicBounds(getMuted(), null, null, null);
    else                            setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
  }

  private Drawable getBlocked() {
    return getDrawable(R.drawable.symbol_block_16);
  }

  private Drawable getMuted() {
    return getDrawable(R.drawable.ic_bell_disabled_16);
  }

  private Drawable getDrawable(@DrawableRes int drawable) {
    Drawable mutedDrawable = ContextUtil.requireDrawable(getContext(), drawable);
    mutedDrawable.setBounds(0, 0, ViewUtil.dpToPx(18), ViewUtil.dpToPx(18));
    DrawableUtil.tint(mutedDrawable, ContextCompat.getColor(getContext(), R.color.signal_icon_tint_secondary));
    return mutedDrawable;
  }
}
