package com.robotutor.iot.accounts.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

const val POLICY_COLLECTION = "policies"

@TypeAlias("Policy")
@Document(POLICY_COLLECTION)
data class Policy(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val policyId: PolicyId,
    val name: String,
)

typealias PolicyId = String
