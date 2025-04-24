package com.overpowerman.api.controller

import com.overpowerman.api.dto.term.CreateTerm
import com.overpowerman.api.dto.term.PublishTerm
import com.overpowerman.api.dto.term.SaveTermDraft
import com.overpowerman.api.service.TermService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/term")
class TermController(private val termService: TermService) {

    @PostMapping
    fun createTerm(@RequestBody createTerm: CreateTerm) {
        termService.createTerm(createTerm)
    }

    @PostMapping("/draft")
    fun saveTermDraft(@RequestBody saveTermDraft: SaveTermDraft) {
        termService.saveTermDraft(saveTermDraft)
    }

    @PostMapping("/publish")
    fun publishTerm(@RequestBody publishTerm: PublishTerm) {
        termService.publishTerm(publishTerm)
    }
}
