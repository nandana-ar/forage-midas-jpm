
package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private UserRecord sender;

    @ManyToOne(optional = false)
    private UserRecord recipient;

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    protected TransactionRecord() {
        // Required by JPA
    }

    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    // Getters omitted for brevity

    @Override
    public String toString() {
        return String.format("Transaction[id=%d, senderId=%d, recipientId=%d, amount=%.2f, timestamp=%s]",
                id, sender.getId(), recipient.getId(), amount, timestamp.toString());
    }
}
