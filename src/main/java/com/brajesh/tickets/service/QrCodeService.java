package com.brajesh.tickets.service;

import com.brajesh.tickets.domain.entities.QrCode;
import com.brajesh.tickets.domain.entities.Ticket;

public interface QrCodeService {

    QrCode generateQrCode(Ticket ticket);
}
