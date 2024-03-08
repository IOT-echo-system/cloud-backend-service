package com.robotutor.iot.utils.models

interface IAuthenticationData

data class UserAuthenticationData(val userId: String, val accountId: String, val roleId: String) : IAuthenticationData
