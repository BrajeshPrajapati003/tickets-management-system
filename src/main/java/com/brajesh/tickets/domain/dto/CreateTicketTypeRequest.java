package com.brajesh.tickets.domain.dto;

public record CreateTicketTypeRequest(

        String name,
        Double price,
        String description,
        Integer totalAvailable

) {
}
