package com.devsu.hackerearth.backend.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import com.devsu.hackerearth.backend.account.exception.InsufficientFundsException;
import com.devsu.hackerearth.backend.account.model.Account;
import com.devsu.hackerearth.backend.account.model.Transaction;
import com.devsu.hackerearth.backend.account.model.dto.TransactionDto;
import com.devsu.hackerearth.backend.account.repository.AccountRepository;
import com.devsu.hackerearth.backend.account.repository.TransactionRepository;
import com.devsu.hackerearth.backend.account.service.TransactionServiceImpl;

public class InsufficientFundsTests {

    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final AccountRepository accountRepository = mock(AccountRepository.class);
    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);

    private final TransactionServiceImpl transactionService = new TransactionServiceImpl(
            transactionRepository,
            accountRepository,
            kafkaTemplate,
            "account.transactions");

    @Test
    void insufficientFundsDoesNotCreateTransactionOrChangeBalance() {
        Account account = new Account();
        account.setId(1L);
        account.setNumber("123");
        account.setType("savings");
        account.setInitialAmount(50.0);
        account.setActive(true);
        account.setClientId(1L);
        account.setCurrentBalance(50.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        TransactionDto request = new TransactionDto(null, null, "DEBIT", -100.0, 0.0, 1L);

        assertThrows(InsufficientFundsException.class, () -> transactionService.create(request));

        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountRepository, never()).save(any(Account.class));
        assertEquals(50.0, account.getCurrentBalance());
    }
}
