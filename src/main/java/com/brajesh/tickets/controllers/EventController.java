package com.brajesh.tickets.controllers;

import com.brajesh.tickets.domain.CreateEventRequest;
import com.brajesh.tickets.domain.dtos.CreateEventRequestDto;
import com.brajesh.tickets.domain.dtos.CreateEventResponseDto;
import com.brajesh.tickets.domain.dtos.ListEventResponseDto;
import com.brajesh.tickets.domain.entities.Event;
import com.brajesh.tickets.mappers.EventMapper;
import com.brajesh.tickets.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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
        UUID userId = parseUserId(jwt);

        // Business logic
        Event event = eventService.createEvent(userId, request);

        // Entity → response DTO
        CreateEventResponseDto response = eventMapper.toDto(event);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ListEventResponseDto>> listEvents(
            @AuthenticationPrincipal Jwt jwt, Pageable pageable
    ){
        UUID userId = parseUserId(jwt);
        Page<Event> events = eventService.listEventsForOrganizer(userId, pageable);
        return ResponseEntity.ok(
                events.map(eventMapper::toListEventResponseDto)
        );

    }

    private UUID parseUserId(Jwt jwt){
        return UUID.fromString(
                Objects.requireNonNull(jwt.getSubject())
        );
    }
}