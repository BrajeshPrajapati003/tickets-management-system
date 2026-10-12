package com.brajesh.tickets.service.impl;

import com.brajesh.tickets.domain.entities.QrCode;
import com.brajesh.tickets.domain.entities.Ticket;
import com.brajesh.tickets.domain.entities.TicketValidation;
import com.brajesh.tickets.domain.entities.enums.QrCodeStatusEnum;
import com.brajesh.tickets.domain.entities.enums.TicketValidationMethod;
import com.brajesh.tickets.domain.entities.enums.TicketValidationStatusEnum;
import com.brajesh.tickets.exception.QrCodeNotFoundException;
import com.brajesh.tickets.exception.TicketNotFoundException;
import com.brajesh.tickets.repository.QrCodeRepository;
import com.brajesh.tickets.repository.TicketRepository;
import com.brajesh.tickets.repository.TicketValidationRepository;
import com.brajesh.tickets.service.TicketValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketValidationServiceImpl implements TicketValidationService {

    private final QrCodeRepository qrCodeRepository;
    private final TicketValidationRepository ticketValidationRepository;
    private final TicketRepository ticketRepository;

    @Override
    public TicketValidation validateTicketByQrCode(UUID qrCodeId) {
        QrCode qrCode = qrCodeRepository.findByIdAndStatus(qrCodeId, QrCodeStatusEnum.ACTIVE)
                .orElseThrow(() -> new QrCodeNotFoundException(
                        String.format("QR Code with ID %s was not found", qrCodeId)
                ));

        Ticket ticket = qrCode.getTicket();

        return validateTicket(ticket, TicketValidationMethod.QR_SCAN);
    }

    private TicketValidation validateTicket(Ticket ticket, TicketValidationMethod ticketValidationMethod) {
        TicketValidation ticketValidation = new TicketValidation();
        ticketValidation.setTicket(ticket);
        ticketValidation.setValidationMethod(ticketValidationMethod);

        TicketValidationStatusEnum ticketValidationStatus = ticket.getValidations().stream()
                .filter(v ->
                        TicketValidationStatusEnum.VALID.equals(v.getStatus()))
                .findFirst()
                .map(v ->
                        TicketValidationStatusEnum.INVALID)
                .orElse(TicketValidationStatusEnum.VALID);

        ticketValidation.setStatus(ticketValidationStatus);

        return ticketValidationRepository.save(ticketValidation);
    }

    @Override
    public TicketValidation validateTicketManually(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(TicketNotFoundException::new);
        return validateTicket(ticket, TicketValidationMethod.MANUAL);
    }
}
