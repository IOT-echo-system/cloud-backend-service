package com.robotutor.iot.routines.models

import com.robotutor.iot.routines.controllers.views.AddRoutineRequest
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val ROUTINE_COLLECTION = "routines"

@TypeAlias("Account")
@Document(ROUTINE_COLLECTION)
data class Routine(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val accountId: String,
    val routineId: RoutineId,
    var name: String,
    var description: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val createdBy: String,
) {
    fun updateName(name: String): Routine {
        this.name = name;
        return this
    }

    companion object {
        fun from(
            accountId: String,
            routineId: RoutineId,
            userId: String,
            addRoutineRequest: AddRoutineRequest,
        ): Routine {
            return Routine(
                accountId = accountId,
                name = addRoutineRequest.name,
                description = addRoutineRequest.description ?: "",
                routineId = routineId,
                createdBy = userId
            )
        }
    }
}

typealias RoutineId = String
