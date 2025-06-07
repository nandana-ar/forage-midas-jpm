package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTransactionListener {

    private static final Logger logger = LoggerFactory.getLogger(KafkaTransactionListener.class);

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group", containerFactory = "transactionKafkaListenerContainerFactory")
    public void listen(Transaction transaction) {
        // Set a breakpoint here to inspect transactions in debugger
        logger.info("Received transaction: {}", transaction);
    }
}
