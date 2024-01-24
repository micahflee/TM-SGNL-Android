package org.tm.archive.components.settings.app.about

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import org.tm.archive.BuildConfig
import org.tm.archive.R


//**TM_SA**//Add this class
class AboutPreferenceFragment : Fragment() {
    private var fullAppName: TextView? = null
    private var version: TextView? = null
    private val doNotSell: TextView? = null
    private val policy: TextView? = null
    private var center: TextView? = null
    private var terms: TextView? = null
    private var supportCenter: TextView? = null
    private val contactUs: LinearLayout? = null
    private val issueR: LinearLayout? = null
    protected var mHandler = Handler()
    private val LOG_NAME = "log.zip"

  override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.settings_about_custom_layout, container, false)
        //name = (TextView)view.view.findViewById(R.id.Name);
        //location = (TextView)view.view.findViewById(R.id.Location);

        initializeAppBar(view)
        initResources(view)
        return view
    }

    private fun initializeAppBar(view: View) {
        val primaryToolbar: Toolbar = view.findViewById(R.id.toolbar)
        (activity as AppCompatActivity?)!!.setSupportActionBar(primaryToolbar)
        assert((activity as AppCompatActivity?)!!.supportActionBar != null)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setTitle(R.string.EditAboutFragment_about)

      primaryToolbar.setNavigationOnClickListener {
        requireActivity().onBackPressed()
      }

    }

    private fun initResources(view: View) {
        fullAppName = view.findViewById(R.id.full_app_name)
        version = view.findViewById(R.id.version)
        terms = view.findViewById(R.id.terms)
        center = view.findViewById(R.id.privacy_center)
        supportCenter = view.findViewById(R.id.TVSupportCenter)
        version!!.setText(
            "version" + " " + getVersionString(
                activity
            )
        )
        setString(fullAppName, R.string.settings_about_title, null)
        setString(terms, R.string.terms_about, null)
        setString(center, R.string.privacy_center_about, null)
        setString(supportCenter, R.string.support_about, null)
    }

    private fun setString(TextView: TextView?, strId: Int, str: String?) {
        val text = if (strId > 0) getString(strId) else str!!
      TextView!!.text = Html.fromHtml(text)
      TextView.movementMethod = LinkMovementMethod.getInstance()
        //        stripUnderlines(view);
    }

    companion object {
        private val TAG = AboutPreferenceFragment::class.java.simpleName
        fun getVersionString(context: Context?): String {
            val versionName: String

            //  versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            versionName = BuildConfig.VERSION_NAME
            return versionName
        }
    }


}