package org.tm.archive.badges

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.use
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import org.tm.archive.R
import org.tm.archive.badges.glide.BadgeSpriteTransformation
import org.tm.archive.badges.models.Badge
import org.tm.archive.database.model.databaseprotos.GiftBadge
import org.tm.archive.glide.GiftBadgeModel
import org.tm.archive.mms.GlideApp
import org.tm.archive.mms.GlideRequests
import org.tm.archive.recipients.Recipient
import org.tm.archive.util.ScreenDensity
import org.tm.archive.util.ThemeUtil

class BadgeImageView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

  private var badgeSize: Int = 0

  init {
    context.obtainStyledAttributes(attrs, R.styleable.BadgeImageView).use {
      badgeSize = it.getInt(R.styleable.BadgeImageView_badge_size, 0)
    }

    isClickable = false
  }

  override fun setOnClickListener(l: OnClickListener?) {
    val wasClickable = isClickable
    super.setOnClickListener(l)
    this.isClickable = wasClickable
  }

  fun setBadgeFromRecipient(recipient: Recipient?) {
    getGlideRequests()?.let {
      setBadgeFromRecipient(recipient, it)
    } ?: clearDrawable()
  }

  fun setBadgeFromRecipient(recipient: Recipient?, glideRequests: GlideRequests) {
    if (recipient == null || recipient.badges.isEmpty()) {
      setBadge(null, glideRequests)
    } else if (recipient.isSelf) {
      val badge = recipient.featuredBadge
      if (badge == null || !badge.visible || badge.isExpired()) {
        setBadge(null, glideRequests)
      } else {
        setBadge(badge, glideRequests)
      }
    } else {
      setBadge(recipient.featuredBadge, glideRequests)
    }
  }

  fun setBadge(badge: Badge?) {
    getGlideRequests()?.let {
      setBadge(badge, it)
    } ?: clearDrawable()
  }

  fun setBadge(badge: Badge?, glideRequests: GlideRequests) {
    if (badge != null) {
      glideRequests
        .load(badge)
        .downsample(DownsampleStrategy.NONE)
        .transform(BadgeSpriteTransformation(BadgeSpriteTransformation.Size.fromInteger(badgeSize), badge.imageDensity, ThemeUtil.isDarkTheme(context)))
        .into(this)

      isClickable = true
    } else {
      glideRequests
        .clear(this)
      clearDrawable()
    }
  }

  fun setGiftBadge(badge: GiftBadge?, glideRequests: GlideRequests) {
    if (badge != null) {
      glideRequests
        .load(GiftBadgeModel(badge))
        .downsample(DownsampleStrategy.NONE)
        .transform(BadgeSpriteTransformation(BadgeSpriteTransformation.Size.fromInteger(badgeSize), ScreenDensity.getBestDensityBucketForDevice(), ThemeUtil.isDarkTheme(context)))
        .into(this)
    } else {
      glideRequests
        .clear(this)
      clearDrawable()
    }
  }

  private fun clearDrawable() {
    setImageDrawable(null)
    isClickable = false
  }

  private fun getGlideRequests(): GlideRequests? {
    return try {
      GlideApp.with(this)
    } catch (e: IllegalArgumentException) {
      // View not attached to an activity or activity destroyed
      null
    }
  }
}
