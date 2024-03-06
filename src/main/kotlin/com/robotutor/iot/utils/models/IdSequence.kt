package com.robotutor.iot.utils.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document



const val ID_SEQUENCE_COLLECTION = "idSequence"

@Document(ID_SEQUENCE_COLLECTION)
data class IdSequence(@Id var idType: String, var sequence: Long)
