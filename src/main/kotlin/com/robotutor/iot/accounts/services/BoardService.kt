package com.robotutor.iot.accounts.services

import com.robotutor.iot.accounts.controllers.views.AddBoardRequest
import com.robotutor.iot.accounts.models.AccountId
import com.robotutor.iot.accounts.models.Board
import com.robotutor.iot.accounts.models.BoardId
import com.robotutor.iot.accounts.models.IdType
import com.robotutor.iot.accounts.repositories.BoardRepository
import com.robotutor.iot.logging.logOnError
import com.robotutor.iot.logging.logOnSuccess
import com.robotutor.iot.mqtt.models.AuditEvent
import com.robotutor.iot.mqtt.services.MqttPublisher
import com.robotutor.iot.utils.audit.auditOnError
import com.robotutor.iot.utils.audit.auditOnSuccess
import com.robotutor.iot.utils.models.UserAuthenticationData
import com.robotutor.iot.utils.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class BoardService(
    private val idGeneratorService: IdGeneratorService,
    private val boardRepository: BoardRepository,
    private val mqttPublisher: MqttPublisher
) {
    fun addNewBoard(addBoardRequest: AddBoardRequest, userAuthenticationData: UserAuthenticationData): Mono<Board> {
        return idGeneratorService.generateId(IdType.BOARD_ID)
            .flatMap { boardId ->
                boardRepository.save(Board.from(boardId, addBoardRequest, userAuthenticationData))
                    .auditOnSuccess(mqttPublisher, AuditEvent.CREATE_BOARD)
                    .auditOnError(mqttPublisher, AuditEvent.CREATE_BOARD)
                    .logOnSuccess(message = "Successfully created new Board")
                    .logOnError(errorMessage = "Failed to create new board")
            }
    }

    fun getBoards(userAuthenticationData: UserAuthenticationData): Flux<Board> {
        return boardRepository.findAllByAccountId(userAuthenticationData.accountId)
    }

    fun updateBoardName(addBoardRequest: AddBoardRequest, boardId: BoardId, accountId: AccountId): Mono<Board> {
        return boardRepository.findByAccountIdAndBoardId(accountId, boardId)
            .flatMap { board ->
                boardRepository.save(board.updateName(addBoardRequest.name))
            }
            .auditOnSuccess(mqttPublisher, AuditEvent.UPDATE_BOARD)
            .auditOnError(mqttPublisher, AuditEvent.UPDATE_BOARD)
            .logOnSuccess(message = "Successfully updated board name")
            .logOnError(errorMessage = "Failed to update board name")
    }

}
