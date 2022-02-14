package org.tm.archive.components.settings.app.subscription.boost

import android.animation.Animator
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import org.tm.archive.R
import org.tm.archive.animation.AnimationCompleteListener
import org.tm.archive.components.settings.PreferenceModel
import org.tm.archive.util.adapter.mapping.LayoutFactory
import org.tm.archive.util.adapter.mapping.MappingAdapter
import org.tm.archive.util.adapter.mapping.MappingViewHolder

/**
 * A simple mapping model to show a boost animation.
 */
object BoostAnimation {

  class Model : PreferenceModel<Model>(isEnabled = true) {
    override fun areItemsTheSame(newItem: Model): Boolean = true
  }

  class ViewHolder(itemView: View) : MappingViewHolder<Model>(itemView) {

    private val lottie: LottieAnimationView = findViewById(R.id.boost_animation_view)

    override fun bind(model: Model) {
      lottie.playAnimation()
      lottie.addAnimatorListener(object : AnimationCompleteListener() {
        override fun onAnimationEnd(animation: Animator?) {
          lottie.removeAnimatorListener(this)
          lottie.setMinAndMaxFrame(30, 91)
          lottie.repeatMode = LottieDrawable.RESTART
          lottie.repeatCount = LottieDrawable.INFINITE
          lottie.frame = 30
          lottie.playAnimation()
        }
      })
    }
  }

  fun register(adapter: MappingAdapter) {
    adapter.registerFactory(Model::class.java, LayoutFactory({ ViewHolder(it) }, R.layout.boost_animation_pref))
  }
}
