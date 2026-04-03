package com.devsu.hackerearth.backend.account.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.devsu.hackerearth.backend.account.exception.InsufficientFundsException;
import com.devsu.hackerearth.backend.account.model.Account;
import com.devsu.hackerearth.backend.account.model.Transaction;
import com.devsu.hackerearth.backend.account.model.dto.BankStatementDto;
import com.devsu.hackerearth.backend.account.model.dto.TransactionDto;
import com.devsu.hackerearth.backend.account.repository.AccountRepository;
import com.devsu.hackerearth.backend.account.repository.TransactionRepository;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String accountTransactionsTopic;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${app.kafka.topic.account-transactions}") String accountTransactionsTopic) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.accountTransactionsTopic = accountTransactionsTopic;
    }

    @Override
    public List<TransactionDto> getAll() {
        return transactionRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDto getById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return toDto(transaction);
    }

    @Override
    public TransactionDto create(TransactionDto transactionDto) {
        if (transactionDto == null || transactionDto.getAccountId() == null) {
            throw new RuntimeException("Account id is required for transaction");
        }

        Account account = accountRepository.findById(transactionDto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        double currentBalance = account.getCurrentBalance();
        double amount = transactionDto.getAmount();
        double newBalance = currentBalance + amount;

        if (newBalance < 0) {
            throw new InsufficientFundsException();
        }

        Transaction transaction = new Transaction();
        transaction.setDate(transactionDto.getDate() != null ? transactionDto.getDate() : new Date());
        transaction.setType(transactionDto.getType());
        transaction.setAmount(amount);
        transaction.setBalance(newBalance);
        transaction.setAccountId(account.getId());
        transaction.setId(null);

        Transaction saved = transactionRepository.save(transaction);
        account.setCurrentBalance(newBalance);
        accountRepository.save(account);

        // Publish a simple TransactionCreated event
        if (kafkaTemplate != null && accountTransactionsTopic != null) {
            kafkaTemplate.send(accountTransactionsTopic, String.valueOf(saved.getId()));
        }

        return toDto(saved);
    }

    @Override
    public List<BankStatementDto> getAllByAccountClientIdAndDateBetween(Long clientId, Date dateTransactionStart,
            Date dateTransactionEnd) {
        if (clientId == null) {
            throw new RuntimeException("clientId is required");
        }

        List<Account> accounts = accountRepository.findByClientId(clientId);
        if (accounts.isEmpty()) {
            throw new RuntimeException("No accounts found for client id: " + clientId);
        }

        List<BankStatementDto> result = new ArrayList<>();
        for (Account account : accounts) {
            List<Transaction> transactions = transactionRepository
                    .findByAccountIdAndDateBetweenOrderByDateAsc(account.getId(), dateTransactionStart, dateTransactionEnd);
            for (Transaction transaction : transactions) {
                BankStatementDto dto = new BankStatementDto(
                        transaction.getDate(),
                        String.valueOf(clientId),
                        account.getNumber(),
                        account.getType(),
                        account.getInitialAmount(),
                        account.isActive(),
                        transaction.getType(),
                        transaction.getAmount(),
                        transaction.getBalance());
                result.add(dto);
            }
        }

        return result;
    }

    @Override
    public TransactionDto getLastByAccountId(Long accountId) {
        Transaction last = transactionRepository
                .findTopByAccountIdOrderByDateDesc(accountId)
                .orElseThrow(() -> new RuntimeException("No transactions found for account"));
        return toDto(last);
    }

    private TransactionDto toDto(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        return new TransactionDto(
                transaction.getId(),
                transaction.getDate(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getBalance(),
                transaction.getAccountId());
    }

    @SuppressWarnings("unused")
    private Transaction toEntity(TransactionDto dto) {
        if (dto == null) {
            return null;
        }
        Transaction transaction = new Transaction();
        transaction.setId(dto.getId());
        transaction.setDate(dto.getDate());
        transaction.setType(dto.getType());
        transaction.setAmount(dto.getAmount());
        transaction.setBalance(dto.getBalance());
        transaction.setAccountId(dto.getAccountId());
        return transaction;
    }

}
