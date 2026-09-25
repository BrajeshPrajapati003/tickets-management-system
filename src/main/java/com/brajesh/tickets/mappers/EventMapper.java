package com.brajesh.tickets.mappers;

import com.brajesh.tickets.domain.CreateEventRequest;
import com.brajesh.tickets.domain.CreateTicketTypeRequest;
import com.brajesh.tickets.domain.dtos.*;
import com.brajesh.tickets.domain.entities.Event;
import com.brajesh.tickets.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface EventMapper {

    CreateTicketTypeRequest fromDto(CreateTicketTypeRequestDto dto);

    CreateEventRequest fromDto(CreateEventRequestDto dto);

    CreateEventResponseDto toDto(Event event);

    ListEventTicketTypeResponseDto toDto(TicketType ticketType);

    ListEventResponseDto toListEventResponseDto(Event event);
}
