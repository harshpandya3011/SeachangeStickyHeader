package com.seachange.healthandsafty.fragment

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.activity.*
import com.seachange.healthandsafty.adapter.SelectUserRecyclerViewAdapter
import com.seachange.healthandsafty.helper.*
import com.seachange.healthandsafty.helper.View.RequestErrorHandlerView
import com.seachange.healthandsafty.helper.interfacelistener.DialogListener
import com.seachange.healthandsafty.model.CaygoSite
import com.seachange.healthandsafty.model.SiteZone
import com.seachange.healthandsafty.model.UserData
import com.seachange.healthandsafty.network.JsonCallBack
import com.seachange.healthandsafty.network.VolleyJsonHelper
import com.seachange.healthandsafty.nfc.ui.NFCSetUpActivity
import com.seachange.healthandsafty.nfconboard.NFCOnBoardingWelcomeActivity
import com.seachange.healthandsafty.presenter.AppPresenter
import com.seachange.healthandsafty.utils.UtilStrings
import com.seachange.healthandsafty.view.AppView
import com.seachange.healthandsafty.viewmodel.ManageUsersViewModel
import kotlinx.android.synthetic.main.fragment_item_list.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.parceler.Parcels
import java.util.*

class SelectUserFragment : BaseFragment(), AppView, RequestErrorHandlerView {

    private val manageUsersViewModel by activityViewModels<ManageUsersViewModel>()

    private lateinit var selectUserRecyclerViewAdapter: SelectUserRecyclerViewAdapter
    private var mZonePref: CheckPreference? = null
    private val SITE_USER_TAG = "site_user_screen_tag"
    private var fromSplash = false
    private var plashWithData = false
    private var fromLogIn = false
    private var statusCode = 0
    private var mCaygoSite: CaygoSite? = null
    private var appPresenter: AppPresenter? = null
    private var errorHandler: RequestErrorHandler? = null

    private var test: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appPresenter = AppPresenter(mCtx, this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mZonePref = CheckPreference(mCtx)
        mCaygoSite = PreferenceHelper.getInstance(mCtx).siteData
        loadDataFromIntent()
        if (fromSplash) {
            if (plashWithData) {
                if (mCaygoSite?.goToNFCSetupFlow(mCtx)==true) {
                    showNFCOnBoarding()
                }
                if (mCaygoSite!=null) {
                    PreferenceHelper.getInstance(mCtx).fetchDataFromServer()
                }
            } else {
                showErrorDialog(statusCode, null)
            }
        } else {
            fetchSiteDataFromServer(false)
//            //test
//            if (test) {
//                showNFCOnBoarding()
//                test = false
//            }
        }
        errorHandler = RequestErrorHandler(this, activity as BaseActivity, false, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageUsersViewModel.caygoUsersLiveData.observe(this, Observer {
            selectUserRecyclerViewAdapter.items = it
        })

        userRecyclyerView.isNestedScrollingEnabled = false
        userRecyclyerView.layoutManager = LinearLayoutManager(userRecyclyerView.context)
        selectUserRecyclerViewAdapter = SelectUserRecyclerViewAdapter(manageUsersViewModel.caygoUsersLiveData.value) {
            //save selected user
            mZonePref!!.saveCaygoUser(it)

            if (PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("passcode_switch", true)) {
                val intent = Intent(requireContext(), PassCodeActivity::class.java)
                intent.putExtra(UtilStrings.MANAGER_HOME, it.userRole == UserData.USER_ROLE_MANAGER)
                intent.putExtra(UtilStrings.OBJECT_USER, Parcels.wrap<UserData>(it))
                startActivity(intent)
            } else {
                val intent = Intent(requireContext(), CaygoHomeActivity::class.java)
                intent.putExtra(UtilStrings.OBJECT_USER, Parcels.wrap<UserData>(it))
                intent.putExtra(UtilStrings.MANAGER_HOME, it.userRole == UserData.USER_ROLE_MANAGER)
                startActivity(intent)
            }
        }
        userRecyclyerView.adapter = selectUserRecyclerViewAdapter
    }

    override fun onResume() {
        super.onResume()
        if (UserDateHelper(mCtx).userLoggedIn) {
            UserDateHelper(mCtx).resetUserLoggedInDetails()
        }
        if (!fromSplash && !fromLogIn) {
            if ((activity as BaseActivity).connected) {
                fetchSiteDataFromServer(false)
                PreferenceHelper.getInstance(mCtx).fetchScheduledData()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fromSplash = false
        fromLogIn = false
    }

    override fun onStop() {
        super.onStop()
        fromSplash = false
        fromLogIn = false
    }

    private fun loadDataFromIntent() {
        if (activity!!.intent.getBooleanExtra(UtilStrings.FROM_SPLASH, false)) {
            fromSplash = activity!!.intent.getBooleanExtra(UtilStrings.FROM_SPLASH, false)
        }

        if (activity!!.intent.getBooleanExtra(UtilStrings.FROM_LOGIN, false)) {
            fromLogIn = activity!!.intent.getBooleanExtra(UtilStrings.FROM_LOGIN, false)
        }

        if (activity!!.intent.getBooleanExtra(UtilStrings.SPLASH_WITH_DATA, false)) {
            plashWithData = activity!!.intent.getBooleanExtra(UtilStrings.FROM_SPLASH, false)
        }

        if (activity!!.intent.hasExtra(UtilStrings.SPLASH_STATUS_CODE)) {
            statusCode = activity!!.intent.getIntExtra(UtilStrings.SPLASH_STATUS_CODE, 0)
        }
    }

    private fun fetchSiteDataFromServer(notify: Boolean) {

        val callBack = object : JsonCallBack {

            override fun callbackJSONObject(result: JSONObject) {

                if (result.has(UtilStrings.STATUS_CODE)) {
                    try {
                        val statusCode = result.getInt(UtilStrings.STATUS_CODE)
                        Logger.info("""get user data from splash, load user screen, statuscode: $statusCode""")
                        if (statusCode == 304) {
                            Logger.info("""$SITE_USER_TAG: statusCode 304 returned, no changes from server""")
                            return
                        } else if (statusCode == 200) {
                            if (result.has(UtilStrings.ETag)) {
                                PreferenceHelper.getInstance(mCtx).saveSiteETag(result.getString(UtilStrings.PREFERENCES_SITE_ETAG))
                                Logger.info("""$SITE_USER_TAG: have updates, response returned, update eTag and response""")
                            }
                            if (result.has(UtilStrings.RESPONSE)) {
                                val gson = Gson()
                                val type = object : TypeToken<CaygoSite>() {}.type
                                val caygoSite = gson.fromJson<CaygoSite>(result.getJSONObject(UtilStrings.RESPONSE).toString(), type)
//                                caygoSite.isUsingNfc = true
                                //The new Endpoint sets user data,
                                //so ignore users from this old endpoint and reset to already saved users
                                caygoSite?.siteUsers = PreferenceHelper.getInstance(mCtx).siteData?.siteUsers?.sortedBy { it.fullName?.toLowerCase(Locale.getDefault()) }
                                PreferenceHelper.getInstance(mCtx).saveSiteData(caygoSite)

                                if (activity !=null) {
                                    (activity as SelectUserActivity).refreshMenuItems()
                                }
                                //if need to need to go into nfc set up flow...
                                if (caygoSite.goToNFCSetupFlow(mCtx)) {
                                    showNFCOnBoarding()
                                }
                                mCaygoSite = caygoSite

                                if (fromSplash || fromLogIn) {
                                    PreferenceHelper.getInstance(mCtx).fetchDataFromServer()
                                }
                                if (notify) {
                                    HazardObserver.getInstance().isSiteDataReceived = true
                                }

                                //
                                //get frequently found hazards on the site data requests
                                //
                                val typeHazard = object : TypeToken<ArrayList<SiteZone>>() {}.type
                                val array = gson.fromJson<ArrayList<SiteZone>>(result.getJSONObject(UtilStrings.RESPONSE).getJSONArray(UtilStrings.ZONES).toString(), typeHazard)
                                PreferenceHelper.getInstance(mCtx).saveFrequentHazards(array)
                                Logger.info(SITE_USER_TAG + ": " + result.getJSONObject(UtilStrings.RESPONSE).toString())
                            }
                        }
                    } catch (e: JSONException) {
                        Logger.info("""$SITE_USER_TAG: $e""")
                    }
                }
            }

            override fun callbackJsonArray(result: JSONArray) {

            }

            override fun callbackErrorCalled(result: VolleyError) {
                //if no valid refresh token, direct to user screen...
                if (result.networkResponse != null && isLive) {
                    val statusCode = result.networkResponse.statusCode
                    if (statusCode == 401 && PreferenceHelper.getInstance(mCtx).refreshToken == null) {
                        goToLogInScreen()
                    }
                }
            }
        }

        VolleyJsonHelper(UtilStrings.CAYGO_ROOT_API, SITE_USER_TAG, callBack, mCtx).getJsonObjectFromVolleyHelper(PreferenceHelper.getInstance(mCtx).requestToken(), PreferenceHelper.getInstance(mCtx).siteETag)
    }

    private fun showErrorDialog(statusCode: Int, error: VolleyError?) {

        val dialogListener = object : DialogListener {

            override fun onDialogPositiveClicked() {
                //fetch data again from server
                fetchSiteDataFromServer(false)
            }

            override fun onDialogNegativeClicked() {

            }

            override fun onDialogResetPasswordClicked() {

            }
        }

        val dialogBuilder = DialogBuilder(statusCode, false)
        if (activity != null) {
            dialogBuilder.showDialog(dialogListener, activity!!, error)
        }
    }

    private fun showNFCOnBoarding() {
        //
        //if setup request failed... show set up page else, show onboarding screen...
        //
        if (PreferenceHelper.getInstance(mCtx).nfcSetupSyncStatus) {
            val intent = Intent(activity, NFCSetUpActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(activity, NFCOnBoardingWelcomeActivity::class.java)
            intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        }
    }

    override fun refreshSuccessfully(result: JSONObject?) {
        fetchSiteDataFromServer(false)
        PreferenceHelper.getInstance(mCtx).fetchScheduledData()
    }

    override fun refreshWithError(error: VolleyError?) {
        goToLogInScreen()
    }

    private fun goToLogInScreen() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
        activity!!.finish()
    }

    override fun errorDialogPositiveClicked() {

    }

    override fun errorDialogNegativeClicked() {

    }

    companion object {

        private val ARG_COLUMN_COUNT = "column-count"
        fun newInstance(columnCount: Int): SelectUserFragment {
            val fragment = SelectUserFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}
