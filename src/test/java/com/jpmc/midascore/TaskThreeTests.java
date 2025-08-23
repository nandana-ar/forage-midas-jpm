package com.jpmc.midascore;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;

import java.util.Optional;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class TaskThreeTests {
    static final Logger logger = LoggerFactory.getLogger(TaskThreeTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private UserRepository userRepository;  // <-- Inject this

    @Test
    void task_three_verifier() throws InterruptedException {
        userPopulator.populate();
        String[] transactionLines = fileLoader.loadStrings("/test_data/mnbvcxz.vbnm");
        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }

        // Wait long enough for all transactions to be processed
        Thread.sleep(20000);

        // Query for the waldorf user
        Optional<UserRecord> waldorfOpt = userRepository.findByNameIgnoreCase("waldorf");
        if (waldorfOpt.isPresent()) {
            UserRecord waldorf = waldorfOpt.get();
            float balance = waldorf.getBalance();
            int roundedBalance = (int) Math.floor(balance);
            logger.info("Waldorf's final balance (rounded down): {}", roundedBalance);
        } else {
            logger.error("User 'waldorf' not found!");
        }

        // End test — no infinite loop
    }
}

