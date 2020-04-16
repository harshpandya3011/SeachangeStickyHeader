package com.seachange.healthandsafty.model

import android.os.Parcelable
import androidx.annotation.IntDef
import com.seachange.healthandsafty.R
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserData(
        val firstName: String?,
        val lastName: String?,
        val fullName: String?,
        val initials: String?,
        val email: String?,
        val avatarUrl: String?,
        val temporaryPasscode: String?,
        val isPasscodeOnly: Boolean?,
        val isPasscodeResetRequired: Boolean?,
        val hashedPasscode: String?,
        val tenantId: Long?,
        @UserRole val userRole: Int?,
        val isDeleted: Boolean?,
        val id: Long,
        val version: Long?,
        val eTag: String?,
        val rank: Int? = null,
        val checks: Int? = null,
        val isInCaygoModule: Boolean? = true
) : Parcelable {

    companion object {
        const val USER_ROLE_NOT_ASSIGNED = 0
        const val USER_ROLE_MANAGER = 1
        const val USER_ROLE_CHAMPION = 2
    }

    @IntDef(value = [USER_ROLE_NOT_ASSIGNED, USER_ROLE_MANAGER, USER_ROLE_CHAMPION])
    @Retention(AnnotationRetention.SOURCE)
    annotation class UserRole

    val userRoleLabel
        get() = when {
            userRole == USER_ROLE_MANAGER -> R.string.caygo_manager
            userRole == USER_ROLE_CHAMPION -> R.string.caygo_champion
            userRole == USER_ROLE_NOT_ASSIGNED -> R.string.not_assigned
            else -> null
        }
}