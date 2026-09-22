package com.brajesh.tickets.controllers;

import com.brajesh.tickets.domain.CreateEventRequest;
import com.brajesh.tickets.domain.dtos.CreateEventRequestDto;
import com.brajesh.tickets.domain.dtos.CreateEventResponseDto;
import com.brajesh.tickets.domain.entities.Event;
import com.brajesh.tickets.mappers.EventMapper;
import com.brajesh.tickets.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventMapper eventMapper;
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<CreateEventResponseDto> createEvent(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateEventRequestDto dto
    ) {

        // DTO → domain request
        CreateEventRequest request = eventMapper.fromDto(dto);

        // Get logged-in user's ID
        UUID userId = UUID.fromString(
                Objects.requireNonNull(jwt.getSubject())
        );

        // Business logic
        Event event = eventService.createEvent(userId, request);

        // Entity → response DTO
        CreateEventResponseDto response = eventMapper.toDto(event);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}