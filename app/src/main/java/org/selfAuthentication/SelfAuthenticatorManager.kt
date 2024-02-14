package org.selfAuthentication

import android.app.Activity
import android.content.Context
import com.tm.authenticatorsdk.selfAuthenticator.*
import com.tm.logger.Log
import org.archive.selfAuthentication.SelfAuthenticatorConstants
import org.tm.archive.ApplicationContext
import org.tm.archive.BuildConfig
import java.util.concurrent.TimeUnit

//In order to change the environment base url call to this method:
//ApiUtil.Companion.selectServerEnvironment(Context)
//The default environment is charlieProduction = https://rest.telemessage.com

object SelfAuthenticatorManager {


    const val SELF_AUTHENTICATION_PREFERENCE_NAME = "SelfAuthenticatorPref"
    const val SELF_AUTHENRICATION_PREF_FIRST_TIME_TRYING_KEY = "firstTimeTrying"
    val SELF_AUTHENRICATION_WHEN_TO_SHOW_FIRST_WARNNING_IN_HOURS = 2 * 24
    val SELF_AUTHENRICATION_WHEN_TO_SHOW_SECOND_WARNNING_IN_HOURS = 7 * 24


    init {
        Log.d("SelfAuthenticatorProcess","class SelfAuthenticatorManager started.")
    }

    lateinit var selfAuthenticator: SelfAuthenticator
    val mSelfAuthenticationDialogBuilder = SelfAuthenticationDialogBuilder()

    fun initAuthenticator(phoneNumber: String) {

        selfAuthenticator = SelfAuthenticator
        Log.d("SelfAuthenticatorProcess", "initAuthenticator - The phone number is: $phoneNumber")
        selfAuthenticator.initSelfAuthenticator(
            AuthenticationAppType.SIGNAL,
            phoneNumber,
          BuildConfig.VERSION_NAME
        )

    }

    fun startAuthentication(context: Context, aIAuthenticationStatus: IAuthenticationStatus) {
        if (!isSelfAuthenticationAlreadyStarted(context)) {
            saveSelfAuthenticationFirstTimeTryingTime(context)
        }
        selfAuthenticator.startSelfAuthentication(aIAuthenticationStatus)
    }

    fun isSelfAuthenticationAlreadyStarted(context: Context): Boolean {
        return getSelfAuthenticationFirstTimeTryingInHours(context) != -1
    }

    fun saveSelfAuthenticationFirstTimeTryingTime(context: Context) {
        if (!isSelfAuthenticationAlreadyStarted(context)) {
          context.getSharedPreferences(SELF_AUTHENTICATION_PREFERENCE_NAME, Context.MODE_PRIVATE).apply {
                edit().apply {
                    putLong(
                        SELF_AUTHENRICATION_PREF_FIRST_TIME_TRYING_KEY,
                        System.currentTimeMillis()
                    )
                    apply()
                }
            }
        }
    }

    fun getSelfAuthenticationFirstTimeTryingInHours(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(SELF_AUTHENTICATION_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val firstTimeInstallInMill =  sharedPreferences.getLong(SELF_AUTHENRICATION_PREF_FIRST_TIME_TRYING_KEY, -1)
        if(firstTimeInstallInMill != (-1).toLong()){
            Log.d("SelfAuthenticatorProcess", "hourDifferenceFromNow() = " + (hourDifferenceFromNow(firstTimeInstallInMill)).toInt())
            return (hourDifferenceFromNow(firstTimeInstallInMill)).toInt()
        }
        Log.d("SelfAuthenticatorProcess", "hourDifferenceFromNow() = " + -1)
        return -1
    }

    private fun hourDifferenceFromNow(millisTime: Long): Long {
        val now = System.currentTimeMillis()
        return TimeUnit.MILLISECONDS.toHours(now - millisTime)
    }

    fun showTheRelevantDialogIfNeeded(
        aContext: Activity
    ) {

        Log.d("SelfAuthenticatorProcess", "getSelfAuthenticationFirstTimeTryingInHours() = " + getSelfAuthenticationFirstTimeTryingInHours(aContext))
        Log.d("SelfAuthenticatorProcess", "getSelfAuthenticationFirstTimeTryingInHours() > SELF_AUTHENRICATION_WHEN_TO_SHOW_FIRST_WARNNING_IN_DAYS = " + (getSelfAuthenticationFirstTimeTryingInHours(aContext) > SELF_AUTHENRICATION_WHEN_TO_SHOW_FIRST_WARNNING_IN_HOURS))
        Log.d("SelfAuthenticatorProcess", "isAppValidationTimePassed() = " + isAppValidationTimePassed(aContext))

        if (getSelfAuthenticationFirstTimeTryingInHours(aContext) > SELF_AUTHENRICATION_WHEN_TO_SHOW_FIRST_WARNNING_IN_HOURS) {
            if (!isAppValidationTimePassed(aContext)) {
                mSelfAuthenticationDialogBuilder.showSelfAuthenticationFirstFailureWarning(aContext)
            } else {
                mSelfAuthenticationDialogBuilder.showSelfAuthenticationSecondFailureWarning(aContext)
            }
        }
    }

    fun isAppValidationTimePassed(aContext: Context): Boolean{
        return getSelfAuthenticationFirstTimeTryingInHours(aContext) > SELF_AUTHENRICATION_WHEN_TO_SHOW_SECOND_WARNNING_IN_HOURS
    }

    fun showLogSentIfNeeded(
        aContext: Activity
    ){
        mSelfAuthenticationDialogBuilder.showSelfAuthenticationFirstFailureWarningLogsSent(aContext)
    }

    fun isOneOfTheAuthenticationDialogIsShowing(): Boolean{
        return mSelfAuthenticationDialogBuilder.isOneOfTheAuthenticationDialogIsShowing()
    }

    fun showProgressDialog(activity: Activity){
        mSelfAuthenticationDialogBuilder.showProgressDialog(activity)
    }

    fun hideProgressDialog(){
        mSelfAuthenticationDialogBuilder.hideProgressDialog()
    }


}