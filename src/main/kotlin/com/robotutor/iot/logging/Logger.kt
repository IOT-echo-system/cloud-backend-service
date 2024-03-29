package com.robotutor.iot.logging

import com.robotutor.iot.logging.serializer.DefaultSerializer.serialize
import org.slf4j.LoggerFactory

class Logger(className: Class<out Any>) {
    private val logger = LoggerFactory.getLogger(className)

    fun info(details: LogDetails) {
        logger.info(serialize(details))
    }

    fun error(details: LogDetails, exception: Throwable) {
        logger.error(serialize(details), exception)
    }
}
