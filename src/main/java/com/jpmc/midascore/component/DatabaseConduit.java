package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DatabaseConduit {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void save(UserRecord user) {
        userRepository.save(user);
    }

    @Transactional
    public void processTransaction(long senderId, long recipientId, float amount) {
        UserRecord sender = userRepository.findById(senderId).orElseThrow(() ->
                new IllegalArgumentException("Sender not found: " + senderId));
        UserRecord recipient = userRepository.findById(recipientId).orElseThrow(() ->
                new IllegalArgumentException("Recipient not found: " + recipientId));

        float senderBalance = sender.getBalance();

        if (senderBalance < amount) {
            System.out.printf("🚫 SKIPPED: Transaction from '%s' (ID: %d, Balance: %.2f) to '%s' (ID: %d) of amount %.2f - insufficient funds%n",
                    sender.getName(), senderId, senderBalance, recipient.getName(), recipientId, amount);
            return;
        }

        sender.setBalance(senderBalance - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        userRepository.saveAll(List.of(sender, recipient));

        TransactionRecord record = new TransactionRecord(sender, recipient, amount);
        transactionRepository.save(record);

        System.out.printf("✅ SUCCESS: %s sent %.2f to %s. New balances — %s: %.2f, %s: %.2f%n",
                sender.getName(), amount, recipient.getName(),
                sender.getName(), sender.getBalance(),
                recipient.getName(), recipient.getBalance());
    }


}
