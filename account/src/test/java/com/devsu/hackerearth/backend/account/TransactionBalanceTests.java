package com.devsu.hackerearth.backend.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import com.devsu.hackerearth.backend.account.model.Account;
import com.devsu.hackerearth.backend.account.model.Transaction;
import com.devsu.hackerearth.backend.account.model.dto.TransactionDto;
import com.devsu.hackerearth.backend.account.repository.AccountRepository;
import com.devsu.hackerearth.backend.account.repository.TransactionRepository;
import com.devsu.hackerearth.backend.account.service.TransactionServiceImpl;

public class TransactionBalanceTests {

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
	void creditTransactionIncreasesBalance() {
		Account account = new Account();
		account.setId(1L);
		account.setNumber("123");
		account.setType("savings");
		account.setInitialAmount(100.0);
		account.setActive(true);
		account.setClientId(1L);
		account.setCurrentBalance(100.0);

		when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
		when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
			Transaction tx = invocation.getArgument(0);
			tx.setId(10L);
			return tx;
		});

		TransactionDto request = new TransactionDto(null, null, "CREDIT", 50.0, 0.0, 1L);
		TransactionDto result = transactionService.create(request);

		assertEquals(150.0, result.getBalance());
		assertEquals(150.0, account.getCurrentBalance());
	}

	@Test
	void debitTransactionDecreasesBalance() {
		Account account = new Account();
		account.setId(1L);
		account.setNumber("123");
		account.setType("savings");
		account.setInitialAmount(200.0);
		account.setActive(true);
		account.setClientId(1L);
		account.setCurrentBalance(200.0);

		when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
		when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
			Transaction tx = invocation.getArgument(0);
			tx.setId(11L);
			return tx;
		});

		TransactionDto request = new TransactionDto(null, null, "DEBIT", -75.0, 0.0, 1L);
		TransactionDto result = transactionService.create(request);

		assertEquals(125.0, result.getBalance());
		assertEquals(125.0, account.getCurrentBalance());
	}
}
