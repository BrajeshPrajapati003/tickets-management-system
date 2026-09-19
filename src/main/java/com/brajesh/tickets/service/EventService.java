package com.brajesh.tickets.service;

import com.brajesh.tickets.domain.dto.CreateEventRequest;
import com.brajesh.tickets.domain.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventService extends JpaRepository<Event, UUID> {
    Event createEvent(UUID organizerId, CreateEventRequest eventRequest);
}
