package com.brajesh.tickets.mappers;

import com.brajesh.tickets.domain.dtos.ListTicketResponseDto;
import com.brajesh.tickets.domain.dtos.ListTicketTicketTypeResponseDto;
import com.brajesh.tickets.domain.entities.Ticket;
import com.brajesh.tickets.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface TicketMapper {

    ListTicketTicketTypeResponseDto toListTicketTicketTypeResponseDto(TicketType ticketType);

    ListTicketResponseDto toListTicketResponseDto(Ticket ticket);
}
