package com.brajesh.tickets.domain.entities;

import com.brajesh.tickets.common.AuditField;
import com.brajesh.tickets.domain.entities.enums.QrCodeStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "qr_codes")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrCode extends AuditField {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private QrCodeStatusEnum status;

    @Column(name = "value", columnDefinition = "TEXT", nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QrCode qrCode = (QrCode) o;
        return Objects.equals(id, qrCode.id) && status == qrCode.status && Objects.equals(value, qrCode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, value);
    }
}
