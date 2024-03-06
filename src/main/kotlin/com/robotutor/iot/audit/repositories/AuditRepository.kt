package com.robotutor.iot.audit.repositories

import com.robotutor.iot.audit.models.Audit
import com.robotutor.iot.audit.models.AuditId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuditRepository : ReactiveCrudRepository<Audit, AuditId>
