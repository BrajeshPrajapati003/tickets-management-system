package com.brajesh.tickets.service.impl;

import com.brajesh.tickets.domain.CreateEventRequest;
import com.brajesh.tickets.domain.entities.Event;
import com.brajesh.tickets.domain.entities.TicketType;
import com.brajesh.tickets.domain.entities.User;
import com.brajesh.tickets.exception.UserNotFoundException;
import com.brajesh.tickets.repository.EventRepository;
import com.brajesh.tickets.repository.UserRepository;
import com.brajesh.tickets.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public Event createEvent(UUID organizerId, CreateEventRequest event) {

        User organizer = userRepository.findById(organizerId)
                .orElseThrow(()-> new UserNotFoundException(
                        String.format("User with ID '%s' not found", organizerId))
                );

        Event eventToCreate = Event.builder()
                .name(event.getName())
                .start(event.getStart())
                .venue(event.getVenue())
                .salesStart(event.getSalesStart())
                .salesEnd(event.getSalesEnd())
                .status(event.getStatus())
                .organizer(organizer)
                .build();

        List<TicketType> ticketTypesToCreate = event.getTicketType().stream().map(
                ticketType -> TicketType.builder()
                        .name(ticketType.getName())
                        .price(ticketType.getPrice())
                        .description(ticketType.getDescription())
                        .totalAvailable(ticketType.getTotalAvailable())
                        .event(eventToCreate)
                        .build()).toList();

        eventToCreate.setTicketTypes(ticketTypesToCreate);

        return eventRepository.save(eventToCreate);

    }

    @Override
    public Page<Event> listEventsForOrganizer(UUID organizerId, Pageable pageable) {
        return eventRepository.findByOrganizerId(organizerId, pageable);
    }

}
