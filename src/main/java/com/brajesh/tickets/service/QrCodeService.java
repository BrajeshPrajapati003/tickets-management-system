package com.brajesh.tickets.service;

import com.brajesh.tickets.domain.entities.QrCode;
import com.brajesh.tickets.domain.entities.Ticket;

import java.util.UUID;

public interface QrCodeService {

    QrCode generateQrCode(Ticket ticket);

    byte[] getQrCodeImageForUserAndTicket(UUID userId, UUID ticketId);
}
