package com.brajesh.tickets.controllers;

import com.brajesh.tickets.domain.dtos.GetPublishedEventDetailsResponseDto;
import com.brajesh.tickets.domain.dtos.ListPublishedEventsResponseDto;
import com.brajesh.tickets.domain.entities.Event;
import com.brajesh.tickets.mappers.EventMapper;
import com.brajesh.tickets.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping( path = "/api/v1/published-events")
@RequiredArgsConstructor
public class PublishedEventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping
    public ResponseEntity<Page<ListPublishedEventsResponseDto>> listPublishedEvents(
            @RequestParam(required = false) String q,
            Pageable pageable
    ){

        Page<Event> events;

        if(null != q && !q.trim().isEmpty()){
            events = eventService.searchPublishedEvents(q, pageable);
        }else{
            events = eventService.listPublishedEvents(pageable);
        }

        return ResponseEntity.ok(
                events.map(eventMapper::toListPublishedEventsResponseDto));
    }

    @GetMapping(path = "/{eventId}")
    public ResponseEntity<GetPublishedEventDetailsResponseDto> getPublishedEventDetails(
            @PathVariable UUID evenId
            ){
        return eventService.getPublishedEvents(evenId)
                .map(eventMapper::toGetPublishedEventDetailsResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
