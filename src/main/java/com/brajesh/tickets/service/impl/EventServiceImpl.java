package com.brajesh.tickets.service.impl;

import com.brajesh.tickets.domain.dto.CreateEventRequest;
import com.brajesh.tickets.domain.entities.Event;
import com.brajesh.tickets.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {


    @Override
    public Event createEvent(UUID organizerId, CreateEventRequest eventRequest) {
        return null;
    }

}
