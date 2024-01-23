package org.tm.archive

import android.content.ContentValues
import android.os.Build
import org.signal.core.util.logging.AndroidLogger
import org.signal.core.util.logging.Log
import org.signal.spinner.Spinner
import org.signal.spinner.Spinner.DatabaseConfig
import org.signal.spinner.SpinnerLogger
import org.tm.archive.database.DatabaseMonitor
import org.tm.archive.database.GV2Transformer
import org.tm.archive.database.GV2UpdateTransformer
import org.tm.archive.database.IsStoryTransformer
import org.tm.archive.database.JobDatabase
import org.tm.archive.database.KeyValueDatabase
import org.tm.archive.database.KyberKeyTransformer
import org.tm.archive.database.LocalMetricsDatabase
import org.tm.archive.database.LogDatabase
import org.tm.archive.database.MegaphoneDatabase
import org.tm.archive.database.MessageBitmaskColumnTransformer
import org.tm.archive.database.MessageRangesTransformer
import org.tm.archive.database.ProfileKeyCredentialTransformer
import org.tm.archive.database.QueryMonitor
import org.tm.archive.database.RecipientTransformer
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.TimestampTransformer
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.logging.PersistentLogger
import org.tm.archive.recipients.Recipient
import org.tm.archive.util.AppSignatureUtil
import org.tm.archive.util.FeatureFlags
import java.util.Locale

class SpinnerApplicationContext : ApplicationContext() {
  override fun onCreate() {
    super.onCreate()

    try {
      Class.forName("dalvik.system.CloseGuard")
        .getMethod("setEnabled", Boolean::class.javaPrimitiveType)
        .invoke(null, true)
    } catch (e: ReflectiveOperationException) {
      throw RuntimeException(e)
    }

    Spinner.init(
      this,
      mapOf(
        "Device" to { "${Build.MODEL} (Android ${Build.VERSION.RELEASE}, API ${Build.VERSION.SDK_INT})" },
        "Package" to { "$packageName (${AppSignatureUtil.getAppSignature(this)})" },
        "App Version" to { "${BuildConfig.VERSION_NAME} (${BuildConfig.CANONICAL_VERSION_CODE}, ${BuildConfig.GIT_HASH})" },
        "Profile Name" to { (if (SignalStore.account().isRegistered) Recipient.self().profileName.toString() else "none") },
        "E164" to { SignalStore.account().e164 ?: "none" },
        "ACI" to { SignalStore.account().aci?.toString() ?: "none" },
        "PNI" to { SignalStore.account().pni?.toString() ?: "none" },
        Spinner.KEY_ENVIRONMENT to { BuildConfig.FLAVOR_environment.uppercase(Locale.US) }
      ),
      linkedMapOf(
        "signal" to DatabaseConfig(
          db = { SignalDatabase.rawDatabase },
          columnTransformers = listOf(MessageBitmaskColumnTransformer, GV2Transformer, GV2UpdateTransformer, IsStoryTransformer, TimestampTransformer, ProfileKeyCredentialTransformer, MessageRangesTransformer, KyberKeyTransformer, RecipientTransformer)
        ),
        "jobmanager" to DatabaseConfig(db = { JobDatabase.getInstance(this).sqlCipherDatabase }, columnTransformers = listOf(TimestampTransformer)),
        "keyvalue" to DatabaseConfig(db = { KeyValueDatabase.getInstance(this).sqlCipherDatabase }),
        "megaphones" to DatabaseConfig(db = { MegaphoneDatabase.getInstance(this).sqlCipherDatabase }),
        "localmetrics" to DatabaseConfig(db = { LocalMetricsDatabase.getInstance(this).sqlCipherDatabase }),
        "logs" to DatabaseConfig(
          db = { LogDatabase.getInstance(this).sqlCipherDatabase },
          columnTransformers = listOf(TimestampTransformer)
        )
      ),
      linkedMapOf(
        StorageServicePlugin.PATH to StorageServicePlugin()
      )
    )

    Log.initialize({ FeatureFlags.internalUser() }, AndroidLogger(), PersistentLogger(this), SpinnerLogger())

    DatabaseMonitor.initialize(object : QueryMonitor {
      override fun onSql(sql: String, args: Array<Any>?) {
        Spinner.onSql("signal", sql, args)
      }

      override fun onQuery(distinct: Boolean, table: String, projection: Array<String>?, selection: String?, args: Array<Any>?, groupBy: String?, having: String?, orderBy: String?, limit: String?) {
        Spinner.onQuery("signal", distinct, table, projection, selection, args, groupBy, having, orderBy, limit)
      }

      override fun onDelete(table: String, selection: String?, args: Array<Any>?) {
        Spinner.onDelete("signal", table, selection, args)
      }

      override fun onUpdate(table: String, values: ContentValues, selection: String?, args: Array<Any>?) {
        Spinner.onUpdate("signal", table, values, selection, args)
      }
    })
  }
}
