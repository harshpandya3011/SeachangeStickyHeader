package com.seachange.healthandsafty.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.mapper.map
import com.seachange.healthandsafty.model.CaygoSite
import com.seachange.healthandsafty.model.UserData
import com.seachange.healthandsafty.network.VolleyNetworkHelper
import com.seachange.healthandsafty.network.model.request.UserRequest
import com.seachange.healthandsafty.network.model.response.UserResponse
import com.seachange.healthandsafty.utils.SingleLiveEvent
import com.seachange.healthandsafty.utils.UtilStrings
import org.json.JSONObject
import kotlin.collections.set

class ManageUsersViewModel(application: Application) : AndroidViewModel(application) {

    val gson = Gson()
    val token: String
    private var siteId: Int?
        get() {
            if (field == null) {
                field = PreferenceHelper.getInstance(getApplication()).siteData?.site_id
            }
            return field
        }

    private var userRequest: Request<*>? = null

    private val _progressGeneratePasscodeLiveData = MutableLiveData<Boolean?>()
    val progressGeneratePasscodeLiveData: LiveData<Boolean?> = _progressGeneratePasscodeLiveData

    private val _progressResetPasscodeLiveData = MutableLiveData<Boolean?>()
    val progressResetPasscodeLiveData: LiveData<Boolean?> = _progressResetPasscodeLiveData

    private val _passCodeGeneratedLiveData = SingleLiveEvent<String?>()
    val passCodeGeneratedLiveData: LiveData<String?> = _passCodeGeneratedLiveData

    private val _resetPassCodeSuccessLiveData = SingleLiveEvent<UserData?>()
    val resetPassCodeSuccessLiveData: LiveData<UserData?> = _resetPassCodeSuccessLiveData

    private val _progressLiveData = MutableLiveData<Boolean?>()
    val progressLiveData: LiveData<Boolean?> = _progressLiveData

    private val _progressDeleteUserLiveData = MutableLiveData<Boolean?>()
    val progressDeleteUserLiveData: LiveData<Boolean?> = _progressDeleteUserLiveData

    private val _progressAddUpdateUserLiveData = MutableLiveData<Boolean?>()
    val progressAddUpdateUserLiveData: LiveData<Boolean?> = _progressAddUpdateUserLiveData

    private val _snackBarMessageLiveData = SingleLiveEvent<Int?>()
    val snackBarMessageLiveData: LiveData<Int?> = _snackBarMessageLiveData

    private val _addEditSuccessLiveData = SingleLiveEvent<Any?>()
    val addEditSuccessLiveData: LiveData<Any?> = _addEditSuccessLiveData

    private val _errorLiveData = SingleLiveEvent<VolleyError?>()
    val errorLiveData: LiveData<VolleyError?> = _errorLiveData

    private val _errorFetchUsersLiveData = SingleLiveEvent<VolleyError?>()
    val errorFetchUsersLiveData: LiveData<VolleyError?> = _errorFetchUsersLiveData

    private val _errorGeneratePasscodeLiveData = SingleLiveEvent<VolleyError?>()
    val errorGeneratePasscodeLiveData: LiveData<VolleyError?> = _errorGeneratePasscodeLiveData

    private val _userResponsesLiveData = MutableLiveData<MutableList<UserResponse>?>()
    val userResponsesLiveData: LiveData<MutableList<UserResponse>?> = _userResponsesLiveData
    private val _usersLiveData = userResponsesLiveData.map { response ->
        val usersData = response?.sortedBy { it.fullName?.toLowerCase() }?.map(UserResponse::map)
        val preferenceHelper = PreferenceHelper.getInstance(getApplication())
        val siteData = preferenceHelper.siteData ?: CaygoSite()
        siteData.siteUsers = usersData
        preferenceHelper.saveSiteData(siteData)
        usersData?.filterNot { it.isDeleted != false }
    } as MutableLiveData<List<UserData>?>
    val usersLiveData: LiveData<List<UserData>?> = _usersLiveData
    val caygoUsersLiveData = _usersLiveData.map {
        it?.filter { item -> item.isInCaygoModule != false }
    }

    var selectedUser: UserData? = null

    init {
        val preferenceHelper = PreferenceHelper.getInstance(application)
        token = preferenceHelper.requestToken().orEmpty()
        val siteData = preferenceHelper.siteData
        siteId = siteData?.site_id

        _usersLiveData.postValue(siteData?.siteUsers)
    }

    fun refreshUsers() {
        _usersLiveData.postValue(PreferenceHelper.getInstance(getApplication()).siteData?.siteUsers)
    }

    fun fetchUsers() {
        val siteId = siteId ?: return
        userRequest?.cancel()
        userRequest = object : StringRequest(
                "${UtilStrings.USERS_TENANTS_API}$siteId",
                Response.Listener<String?> {
                    _progressLiveData.postValue(false)
                    _userResponsesLiveData.postValue(
                            gson.fromJson(it, object : TypeToken<List<UserResponse>>() {}.type)
                    )
                },
                Response.ErrorListener {
                    _progressLiveData.postValue(false)
                    //TODO negative scenarios
                    _errorFetchUsersLiveData.postValue(it)
                }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = super.getHeaders().toMutableMap()
                headers["Authorization"] = token
                return headers
            }
        }.apply {
            _progressLiveData.postValue(true)
            VolleyNetworkHelper.getInstance(getApplication())
                    .addToRequestQueue(this)
        }
    }

    fun deleteUser(userId: Long) {
        userRequest?.cancel()
        userRequest = object : StringRequest(
                Method.DELETE,
                "${UtilStrings.USERS_API}/$userId",
                Response.Listener<String?> {
                    _progressDeleteUserLiveData.postValue(false)
                    userResponsesLiveData.value?.indexOfFirst { it.id == userId }.takeIf { it != -1 }?.let { userIndex ->
                        val users = userResponsesLiveData.value
                        users?.removeAt(userIndex)
                        _userResponsesLiveData.value = users
                    }
                    _snackBarMessageLiveData.postValue(R.string.user_deleted_successfully)
                },
                Response.ErrorListener {
                    _progressDeleteUserLiveData.postValue(false)
                    _errorLiveData.postValue(it)
                }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = super.getHeaders().toMutableMap()
                headers["Authorization"] = token
                return headers
            }
        }.apply {
            _progressDeleteUserLiveData.postValue(true)
            VolleyNetworkHelper.getInstance(getApplication())
                    .addToRequestQueue(this)
        }
    }

    fun editUser(
            userId: Long,
            tenantId: Long?,
            firstName: String?,
            lastName: String?,
            email: String?,
            @UserData.UserRole userRole: Int?,
            isPasscodeOnly: Boolean?,
            tenantRoleId: Int
    ) {
        val modules = userRole?.let {
            listOf(
                    UserRequest.Module(
                            UserResponse.Tenant.Module.CAYGO_DETAIL_MODULE,
                            if (it == UserData.USER_ROLE_MANAGER) {
                                UserResponse.Tenant.Module.Role.MANAGER_ROLE_ID
                            } else {
                                UserResponse.Tenant.Module.Role.CHAMPION_ROLE_ID
                            }
                    )
            )
        }
        userRequest?.cancel()
        userRequest = object : JsonObjectRequest(
                Method.PUT,
                "${UtilStrings.USERS_API}/$userId/v2?version=1.2",
                JSONObject(
                        gson.toJson(
                                UserRequest(
                                        tenantId,
                                        firstName,
                                        lastName,
                                        email,
                                        isPasscodeOnly,
                                        modules,
                                        tenantRoleId
                                )
                        )
                ),
                Response.Listener<JSONObject?> { jsonResponse ->
                    jsonResponse?.let { response ->
                        userResponsesLiveData.value?.indexOfFirst { it.id == userId }.takeIf { it != -1 }?.let { userIndex ->
                            _userResponsesLiveData.value = userResponsesLiveData.value?.apply {
                                set(userIndex, gson.fromJson(response.toString(), UserResponse::class.java))
                            }
                        }
                    }
                    _progressAddUpdateUserLiveData.postValue(false)
                    _addEditSuccessLiveData.postValue(null)
                    _snackBarMessageLiveData.postValue(R.string.user_updated_successfully)
                },
                Response.ErrorListener {
                    _progressAddUpdateUserLiveData.postValue(false)
                    _errorLiveData.postValue(it)
                }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = super.getHeaders().toMutableMap()
                headers["Authorization"] = token
                return headers
            }
        }.apply {
            _progressAddUpdateUserLiveData.postValue(true)
            VolleyNetworkHelper.getInstance(getApplication())
                    .addToRequestQueue(this)
        }
    }

    fun addUser(
            firstName: String,
            lastName: String,
            email: String?,
            @UserData.UserRole userRole: Int,
            isPasscodeOnly: Boolean,
            tenantRoleId:Int
    ) {
        val siteId = siteId ?: return
        userRequest?.cancel()
        userRequest = object : JsonObjectRequest(
                Method.POST,
                "${UtilStrings.USERS_API}?version=1.2",
                JSONObject(
                        gson.toJson(
                                UserRequest(
                                        siteId.toLong(),
                                        firstName,
                                        lastName,
                                        email,
                                        isPasscodeOnly,
                                        listOf(
                                                UserRequest.Module(
                                                        UserResponse.Tenant.Module.CAYGO_DETAIL_MODULE,
                                                        if (userRole == UserData.USER_ROLE_MANAGER) {
                                                            UserResponse.Tenant.Module.Role.MANAGER_ROLE_ID
                                                        } else {
                                                            UserResponse.Tenant.Module.Role.CHAMPION_ROLE_ID
                                                        }
                                                )
                                        ),tenantRoleId
                                )
                        )
                ),
                Response.Listener<JSONObject?> {
                    it?.let { response ->
                        _userResponsesLiveData.value = userResponsesLiveData.value?.apply {
                            add(0, gson.fromJson(response.toString(), UserResponse::class.java))
                        }
                    }
                    _progressAddUpdateUserLiveData.postValue(false)
                    _addEditSuccessLiveData.postValue(null)
                    _snackBarMessageLiveData.postValue(R.string.user_added_successfully)
                },
                Response.ErrorListener {
                    _progressAddUpdateUserLiveData.postValue(false)
                    _errorLiveData.postValue(it)
                }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = super.getHeaders().toMutableMap()
                headers["Authorization"] = token
                return headers
            }
        }.apply {
            _progressAddUpdateUserLiveData.postValue(true)
            VolleyNetworkHelper.getInstance(getApplication())
                    .addToRequestQueue(this)
        }
    }

    fun generatePasscode(userData: UserData, tenantId: Long) {
        if (userData.temporaryPasscode != null) {
            _progressGeneratePasscodeLiveData.postValue(false)
            _passCodeGeneratedLiveData.postValue(
                    userData.temporaryPasscode
            )
            return
        }

        userRequest?.cancel()
        userRequest = object : JsonObjectRequest(
                Method.PUT,
                "${UtilStrings.USERS_API}/${userData.id}/generate-passcode",
                JSONObject(mutableMapOf("tenantId" to tenantId.toString())),
                Response.Listener<JSONObject> { response ->
                    val temporaryPasscode: String? = response.optString("temporaryPasscode", null)
                    val isPasscodeResetRequired: Boolean = response.optBoolean("isPasscodeResetRequired")
                    _userResponsesLiveData.value = userResponsesLiveData.value?.apply {
                        val indexOfFirst = indexOfFirst { item -> userData.id == item.id }
                        if (indexOfFirst != -1) {
                            set(indexOfFirst, get(indexOfFirst).copy(temporaryPasscode = temporaryPasscode, isPasscodeResetRequired = isPasscodeResetRequired))
                            _passCodeGeneratedLiveData.postValue(temporaryPasscode)
                        }
                    }
                    _progressGeneratePasscodeLiveData.postValue(false)
                },
                Response.ErrorListener {
                    _progressGeneratePasscodeLiveData.postValue(false)
                    _errorLiveData.postValue(it)
                    _errorGeneratePasscodeLiveData.postValue(it)
                }
        ) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = super.getHeaders().toMutableMap()
                headers["Authorization"] = token
                return headers
            }
        }.apply {
            _progressGeneratePasscodeLiveData.postValue(true)
            VolleyNetworkHelper.getInstance(getApplication())
                    .addToRequestQueue(this)
        }
    }

    fun resetPasscode(userData: UserData, tenantId: Long, newPassCode: String) {
        userRequest?.cancel()
        userRequest = object : JsonObjectRequest(
                Method.PUT,
                "${UtilStrings.USERS_API}/${userData.id}/passcode",
                JSONObject(
                        mutableMapOf(
                                "tenantId" to tenantId.toString(),
                                "passcode" to newPassCode,
                                "passcodeConfirmed" to newPassCode
                        )
                ),
                Response.Listener<JSONObject> { response ->
                    val temporaryPasscode: String? = response.optString("temporaryPasscode", null)
                    val hashedPasscode: String? = response.optString("hashedPasscode", null)
                    val isPasscodeResetRequired: Boolean = response.optBoolean("isPasscodeResetRequired")
                    _usersLiveData.value = usersLiveData.value?.toMutableList()?.apply {
                        val indexOfFirst = indexOfFirst { item -> userData.id == item.id }
                        if (indexOfFirst != -1) {
                            set(indexOfFirst, get(indexOfFirst).copy(hashedPasscode = hashedPasscode, temporaryPasscode = temporaryPasscode, isPasscodeResetRequired = isPasscodeResetRequired))
                            val preferenceHelper = PreferenceHelper.getInstance(getApplication())
                            val siteData = preferenceHelper.siteData
                            if (siteData != null) {
                                val preferenceIndexOfFirst = siteData.siteUsers?.indexOfFirst { item -> item.id == userData.id }
                                        ?: -1
                                if (preferenceIndexOfFirst != -1) {
                                    siteData.siteUsers?.set(preferenceIndexOfFirst, get(indexOfFirst))
                                    preferenceHelper.saveSiteData(siteData)
                                }
                            }
                            _resetPassCodeSuccessLiveData.postValue(get(indexOfFirst))
                        }
                    }
                    _progressResetPasscodeLiveData.postValue(false)
                },
                Response.ErrorListener {
                    _progressResetPasscodeLiveData.postValue(false)
                    _errorLiveData.postValue(it)
                }
        ) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = super.getHeaders().toMutableMap()
                headers["Authorization"] = token
                return headers
            }
        }.apply {
            _progressResetPasscodeLiveData.postValue(true)
            VolleyNetworkHelper.getInstance(getApplication())
                    .addToRequestQueue(this)
        }
    }

    fun areInputsValidAndUpdated(firstName: String?, lastName: String?, email: String?, @UserData.UserRole userRole: Int, isPasscodeOnly: Boolean): Boolean {
        return !firstName.isNullOrBlank()
                && !lastName.isNullOrBlank()
                && (isPasscodeOnly || !email.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
                && (selectedUser?.firstName.orEmpty() != firstName
                || selectedUser?.lastName.orEmpty() != lastName
                || selectedUser?.email.orEmpty() != email
                || selectedUser?.isPasscodeOnly != isPasscodeOnly
                || selectedUser?.userRole != userRole)
    }

    override fun onCleared() {
        cancelUserRequest()
    }

    fun cancelUserRequest() {
        userRequest?.cancel()
        userRequest = null
    }

}