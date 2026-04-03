package com.devsu.hackerearth.backend.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.devsu.hackerearth.backend.account.controller.AccountController;
import com.devsu.hackerearth.backend.account.model.dto.AccountDto;
import com.devsu.hackerearth.backend.account.service.AccountService;

public class sampleTest {

	private final AccountService accountService = mock(AccountService.class);
	private final AccountController accountController = new AccountController(accountService);

	@Test
	void createAccountTest() {
		AccountDto newAccount = new AccountDto(1L, "number", "savings", 0.0, true, 1L);
		AccountDto createdAccount = new AccountDto(1L, "number", "savings", 0.0, true, 1L);
		when(accountService.create(newAccount)).thenReturn(createdAccount);

		ResponseEntity<AccountDto> response = accountController.create(newAccount);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(createdAccount, response.getBody());
	}

	@Test
	void deleteAccountTest() {
		Long id = 1L;
		doNothing().when(accountService).deleteById(id);

		ResponseEntity<Void> response = accountController.delete(id);

		verify(accountService).deleteById(id);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}
}

