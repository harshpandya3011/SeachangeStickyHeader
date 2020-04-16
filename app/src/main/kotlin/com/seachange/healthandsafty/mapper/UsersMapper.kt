package com.seachange.healthandsafty.mapper

import com.seachange.healthandsafty.model.UserData
import com.seachange.healthandsafty.network.model.response.UserResponse

fun UserResponse.map(): UserData {
    val caygoModule = tenants?.flatMap { tenant ->
        tenant.modules.orEmpty()
    }?.find { module ->
        module.id == UserResponse.Tenant.Module.CAYGO_DETAIL_MODULE
    }
    return UserData(
            firstName,
            lastName,
            fullName,
            initials,
            email,
            avatarUrl,
            temporaryPasscode,
            isPasscodeOnly,
            isPasscodeResetRequired,
            hashedPasscode,
            tenants?.firstOrNull()?.id,
            when (caygoModule?.role?.id) {
                UserResponse.Tenant.Module.Role.MANAGER_ROLE_ID -> UserData.USER_ROLE_MANAGER
                UserResponse.Tenant.Module.Role.CHAMPION_ROLE_ID -> UserData.USER_ROLE_CHAMPION
                else -> UserData.USER_ROLE_NOT_ASSIGNED
            },
            isDeleted,
            id,
            version,
            eTag,
            isInCaygoModule = caygoModule != null
    )
}