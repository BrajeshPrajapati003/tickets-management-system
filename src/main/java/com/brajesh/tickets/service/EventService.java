package com.brajesh.tickets.service;

import com.brajesh.tickets.domain.CreateEventRequest;
import com.brajesh.tickets.domain.entities.Event;

import java.util.UUID;

public interface EventService{
    Event createEvent(UUID organizerId, CreateEventRequest eventRequest);
}
