package com.brajesh.tickets.mappers;

import com.brajesh.tickets.domain.CreateEventRequest;
import com.brajesh.tickets.domain.CreateTicketTypeRequest;
import com.brajesh.tickets.domain.dtos.CreateEventRequestDto;
import com.brajesh.tickets.domain.dtos.CreateEventResponseDto;
import com.brajesh.tickets.domain.dtos.CreateTicketTypeRequestDto;
import com.brajesh.tickets.domain.entities.Event;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface EventMapper {

    CreateTicketTypeRequest fromDto(CreateTicketTypeRequestDto dto);

    CreateEventRequest fromDto(CreateEventRequestDto dto);

    CreateEventResponseDto toDto(Event event);
}
