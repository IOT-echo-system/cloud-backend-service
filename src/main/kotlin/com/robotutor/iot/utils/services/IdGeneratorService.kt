package com.robotutor.iot.utils.services

import com.robotutor.iot.utils.models.ID_SEQUENCE_COLLECTION
import com.robotutor.iot.utils.models.IdSequence
import com.robotutor.iot.utils.models.IdSequenceType
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class IdGeneratorService(private val reactiveMongoTemplate: ReactiveMongoTemplate) {

    fun generateId(idType: IdSequenceType): Mono<String> {
        return reactiveMongoTemplate.findAndModify(
            Query.query(Criteria.where("_idType").`is`(idType)),
            Update().inc("sequence", 1),
            FindAndModifyOptions.options().returnNew(true).upsert(true),
            IdSequence::class.java,
            ID_SEQUENCE_COLLECTION
        ).map { idSequence ->
            idSequence.sequence.toString().padStart(idType.length, '0')
        }
    }
}



