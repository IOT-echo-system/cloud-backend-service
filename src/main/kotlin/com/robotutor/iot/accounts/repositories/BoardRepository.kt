package com.robotutor.iot.accounts.repositories

import com.robotutor.iot.accounts.models.Board
import com.robotutor.iot.accounts.models.BoardId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BoardRepository :ReactiveCrudRepository<Board, BoardId>{

}
