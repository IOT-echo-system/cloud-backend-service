package com.robotutor.iot.communication.service

import com.robotutor.iot.communication.config.CommunicationConfig
import com.robotutor.iot.logging.LogDetails
import com.robotutor.iot.logging.Logger
import com.robotutor.iot.mqtt.models.*
import com.robotutor.iot.mqtt.services.MqttPublisher
import org.springframework.core.io.ClassPathResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.util.StreamUtils
import java.nio.charset.StandardCharsets

@Service
class MailService(
    private val mailSender: JavaMailSender,
    private val communicationConfig: CommunicationConfig,
    private val mqttPublisher: MqttPublisher
) {
    private val logger = Logger(this::class.java)
    fun sendEmail(message: CommunicationMessage) {
        try {
            val contentAndSubject = getTemplate(message.type)
            val mimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(mimeMessage, true)
            helper.setFrom(communicationConfig.from)
            helper.setTo(message.to)
            helper.setText(updateContent(contentAndSubject.first, message.metadata), true)
            helper.setSubject(contentAndSubject.second)
            println(mailSender.toString())
            mailSender.send(mimeMessage)
            logger.info(LogDetails(message = "Successfully sent email"))
            mqttPublisher.publish(
                MqttTopicName.AUDIT, AuditMessage(
                    status = AuditStatus.SUCCESS,
                    userId = message.userId,
                    metadata = mapOf("type" to message.type),
                    event = AuditEvent.SEND_EMAIL,
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            logger.info(LogDetails(message = "Failed to send email"))
            mqttPublisher.publish(
                MqttTopicName.AUDIT, AuditMessage(
                    status = AuditStatus.FAILURE,
                    userId = message.userId,
                    metadata = mapOf("type" to message.type),
                    event = AuditEvent.SEND_EMAIL,
                )
            )
        }
    }

    private fun updateContent(content: String, metadata: Map<String, Any>): String {
        var updatedContent = content
        metadata.entries.forEach {
            val regex = Regex("\\{\\{${it.key}}}", RegexOption.IGNORE_CASE)
            updatedContent = updatedContent.replace(regex, it.value.toString())
        }
        return updatedContent
    }

    private fun getTemplate(communicationType: CommunicationType): Pair<String, String> {
        val templatePathAndSubject = when (communicationType) {
            CommunicationType.OTP -> Pair("otpEmail.html", "One time password to verify your account")
        }
        val resource = ClassPathResource("communications/" + templatePathAndSubject.first)
        val templateStream = resource.inputStream
        val content = StreamUtils.copyToString(templateStream, StandardCharsets.UTF_8)
        return Pair(content, templatePathAndSubject.second)
    }
}
