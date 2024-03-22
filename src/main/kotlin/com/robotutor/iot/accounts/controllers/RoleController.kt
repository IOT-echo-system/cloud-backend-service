package com.robotutor.iot.accounts.controllers

import com.robotutor.iot.accounts.controllers.views.RoleView
import com.robotutor.iot.accounts.models.RoleId
import com.robotutor.iot.accounts.services.RoleService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/roles")
class RoleController(private val roleService: RoleService) {

    @GetMapping
    fun getRoles(@RequestParam roleIds: List<RoleId>): Flux<RoleView> {
        return roleService.getRoles(roleIds).map { RoleView.from(it) }
    }
}
