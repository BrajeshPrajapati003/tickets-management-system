package com.brajesh.tickets.domain.dto;

import com.brajesh.tickets.domain.entities.User;
import com.brajesh.tickets.domain.entities.enums.EventStatusEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record CreateEventRequest(

        String name,

        LocalDateTime start,
        LocalDateTime end,

        String venue,
        LocalDateTime salesStart,
        LocalDateTime salesEnd,

        EventStatusEnum status,

        List<CreateTicketTypeRequest> ticketTypes

) {

    public CreateEventRequest{
        ticketTypes = ticketTypes() != null ? ticketTypes()
                : new ArrayList<>();
    }
}
