package com.brajesh.tickets.domain.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public class UpdateTicketTypeResponseDto {
    private UUID id;
    private String name;
    private Double price;
    private String description;
    private Integer totalAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
