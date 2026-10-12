package com.brajesh.tickets.service.impl;

import com.brajesh.tickets.domain.entities.Ticket;
import com.brajesh.tickets.domain.entities.TicketType;
import com.brajesh.tickets.domain.entities.User;
import com.brajesh.tickets.domain.entities.enums.TicketStatusEnum;
import com.brajesh.tickets.exception.TicketTypeNotFoundException;
import com.brajesh.tickets.exception.TicketsSoldOutException;
import com.brajesh.tickets.exception.UserNotFoundException;
import com.brajesh.tickets.repository.TicketRepository;
import com.brajesh.tickets.repository.TicketTypeRepository;
import com.brajesh.tickets.repository.UserRepository;
import com.brajesh.tickets.service.QrCodeService;
import com.brajesh.tickets.service.TicketTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final QrCodeService qrCodeService;

    @Override
    @Transactional
    public Ticket purchaseTicket(UUID userId, UUID ticketTypeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException(
                        String.format("User with ID %s was not found", userId)
                ));

        TicketType ticketType = ticketTypeRepository.findByIdWithLock(ticketTypeId)
                .orElseThrow(()-> new TicketTypeNotFoundException(
                        String.format("Ticket type with ID %s was not found", ticketTypeId)
                ));

        int purchasedTickets = ticketRepository.countByTicketTypeId(ticketType.getId());
        Integer totalAvailable = ticketType.getTotalAvailable();

        if (purchasedTickets + 1 > totalAvailable){
            throw new TicketsSoldOutException();
        }

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatusEnum.PURCHASED);
        ticket.setTicketType(ticketType);
        ticket.setPurchaser(user);

        Ticket savedTicket = ticketRepository.save(ticket);
        qrCodeService.generateQrCode(savedTicket);

        return savedTicket;
    }
}
