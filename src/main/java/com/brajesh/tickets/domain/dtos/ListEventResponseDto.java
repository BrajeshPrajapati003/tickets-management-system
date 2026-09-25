package com.brajesh.tickets.domain.dtos;

import com.brajesh.tickets.domain.entities.enums.EventStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.jmx.export.annotation.ManagedNotifications;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListEventResponseDto {

    private UUID id;
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
    private String venue;
    private LocalDateTime salesStart;
    private LocalDateTime salesEnd;
    private EventStatusEnum status;
    private List<ListEventTicketTypeResponseDto> ticketTypes = new ArrayList<>();
}
