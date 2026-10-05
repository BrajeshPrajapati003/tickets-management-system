package com.brajesh.tickets.service.impl;

import com.brajesh.tickets.domain.CreateEventRequest;
import com.brajesh.tickets.domain.UpdateEventRequest;
import com.brajesh.tickets.domain.UpdateTicketTypeRequest;
import com.brajesh.tickets.domain.entities.Event;
import com.brajesh.tickets.domain.entities.TicketType;
import com.brajesh.tickets.domain.entities.User;
import com.brajesh.tickets.domain.entities.enums.EventStatusEnum;
import com.brajesh.tickets.exception.EventNotFoundException;
import com.brajesh.tickets.exception.EventUpdateException;
import com.brajesh.tickets.exception.TicketTypeNotFoundException;
import com.brajesh.tickets.exception.UserNotFoundException;
import com.brajesh.tickets.repository.EventRepository;
import com.brajesh.tickets.repository.UserRepository;
import com.brajesh.tickets.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
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

    @Override
    public Optional<Event> getEventForOrganizer(UUID organizerId, UUID id) {
        return eventRepository.findByIdAndOrganizerId(id, organizerId);
    }

    @Override
    @Transactional
    public Event updateEventForOrganizer(UUID organizerId, UUID id, UpdateEventRequest event) {
        if(null == event.getId()){
            throw new EventUpdateException("Event ID cannot be null");
        }

        if(!id.equals(event.getId())){
            throw new EventUpdateException("Cannot update the ID of an event");
        }

        Event existingEvent = eventRepository.findByIdAndOrganizerId(id, organizerId)
                .orElseThrow(()-> new EventNotFoundException("Event with ID '%s' does not exist"));

        existingEvent.setName(event.getName());
        existingEvent.setStart(event.getStart());
        existingEvent.setEnd(event.getEnd());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setSalesStart(event.getSalesStart());
        existingEvent.setSalesEnd(event.getSalesEnd());
        existingEvent.setStatus(event.getStatus());

        Set<UUID> requestTicketTypeIds = event.getTicketTypes()
                .stream()
                .map(UpdateTicketTypeRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existingEvent.getTicketTypes().removeIf(existingTicketType ->
                !requestTicketTypeIds.contains(existingTicketType.getId())
        );

        Map<UUID, TicketType> existingTicketTypesIndex = existingEvent.getTicketTypes().stream()
                .collect(Collectors.toMap(TicketType::getId, Function.identity()));

        for(UpdateTicketTypeRequest ticketType : event.getTicketTypes()){

            if(null == ticketType.getId()){
                // Create
                TicketType ticketTypeToCreate = TicketType.builder()
                        .name(ticketType.getName())
                        .price(ticketType.getPrice())
                        .description(ticketType.getDescription())
                        .totalAvailable(ticketType.getTotalAvailable())
                        .event(existingEvent)
                        .build();

                existingEvent.getTicketTypes().add(ticketTypeToCreate);

            } else if (existingTicketTypesIndex.containsKey(ticketType.getId())) {
                // Update
                TicketType existingTicketType = existingTicketTypesIndex.get(ticketType.getId());
                existingTicketType.setName(ticketType.getName());
                existingTicketType.setPrice(ticketType.getPrice());
                existingTicketType.setDescription(ticketType.getDescription());
                existingTicketType.setTotalAvailable(ticketType.getTotalAvailable());

            }else{
                throw new TicketTypeNotFoundException(String.format(
                        "Ticket type with ID '%s' does not exist", ticketType.getId()
                ));
            }
        }

        return eventRepository.save(existingEvent);
    }

    @Override
    @Transactional
    public void deleteEventForOrganizer(UUID organizerId, UUID id) {
        getEventForOrganizer(organizerId, id).ifPresent(eventRepository::delete);
    }

    @Override
    public Page<Event> listPublishedEvents(Pageable pageable) {
        return eventRepository.findByStatus(EventStatusEnum.PUBLISHED, pageable);
    }

    @Override
    public Page<Event> searchPublishedEvents(String query, Pageable pageable) {
        return eventRepository.searchEvents(query, pageable);
    }

    @Override
    public Optional<Event> getPublishedEvents(UUID id) {
        return eventRepository.findByIdAndStatus(id, EventStatusEnum.PUBLISHED);
    }


}
