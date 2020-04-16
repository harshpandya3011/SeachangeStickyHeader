package com.seachange.healthandsafty.network.model.request

data class UserRequest(
        val tenantId: Long?,
        val firstName: String?,
        val lastName: String?,
        val email: String?,
        val isPasscodeOnly: Boolean?,
        val modules: List<Module>?,
        val tenantRoleId: Int? = 1
) {
    data class Module(
            val id: String,
            val roleid: String
    )
}