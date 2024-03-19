package com.robotutor.iot.utils.models

import com.robotutor.iot.utils.gateway.views.UserAuthenticationResponseData

interface IAuthenticationData

data class UserAuthenticationData(val userId: String, val accountId: String, val roleId: String) : IAuthenticationData {
    companion object {
        fun from(userAuthenticationResponseData: UserAuthenticationResponseData): UserAuthenticationData {
            return UserAuthenticationData(
                userId = userAuthenticationResponseData.userId,
                accountId = userAuthenticationResponseData.projectId,
                roleId = userAuthenticationResponseData.roleId
            )
        }
    }
}
