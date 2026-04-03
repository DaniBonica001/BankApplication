package com.devsu.hackerearth.backend.account;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.devsu.hackerearth.backend.account.model.Account;
import com.devsu.hackerearth.backend.account.repository.AccountRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class InsufficientFundsIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void insufficientFundsRequestReturnsSaldoNoDisponibleAndDoesNotChangeBalance() throws Exception {
        Account account = new Account();
        account.setNumber("ACC-001");
        account.setType("SAVINGS");
        account.setInitialAmount(100.0);
        account.setActive(true);
        account.setClientId(1L);
        account.setCurrentBalance(100.0);

        Account savedAccount = accountRepository.save(account);

        String requestBody = "{" +
                "\"accountId\":" + savedAccount.getId() + "," +
                "\"type\":\"DEBIT\"," +
                "\"amount\":-200.0" +
                "}";

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Saldo no disponible"));

        Account reloaded = accountRepository.findById(savedAccount.getId()).orElseThrow();
        // Balance must remain unchanged
        org.junit.jupiter.api.Assertions.assertEquals(100.0, reloaded.getCurrentBalance());
    }
}
