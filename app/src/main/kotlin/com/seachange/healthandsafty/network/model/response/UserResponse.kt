package com.seachange.healthandsafty.network.model.response

data class UserResponse(
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
        val tenants: List<Tenant>?,
        val isDeleted: Boolean?,
        val id: Long,
        val version: Long?,
        val eTag: String?
) {

    data class Tenant(
            val id: Long,
            val name: String?,
            val role: Role?,
            val modules: List<Module>?
    ) {

        data class Role(
                val id: Long,
                val label: String?
        )

        data class Module(
                val id: String,
                val role: Role
        ) {

            companion object {
                const val CAYGO_DETAIL_MODULE = "09fff2c7-4bb9-424d-ae0f-5f6ad4d83cae"
            }

            data class Role(
                    val id: String,
                    val label: String?
            ) {
                companion object {
                    const val MANAGER_ROLE_ID = "10256cae-6fc2-4464-aad6-6704bd50e4c9"
                    const val CHAMPION_ROLE_ID = "7110211a-83e8-498e-832b-ecd503443794"
                }
            }
        }
    }
}