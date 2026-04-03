package com.devsu.hackerearth.backend.account;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ReportErrorTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void invalidDateRangeReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/transactions/client/1/report")
                .param("dateTransactionStart", "2024-12-31")
                .param("dateTransactionEnd", "2024-01-01")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid date range"));
    }

    @Test
    void invalidClientIdReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/transactions/client/999/report")
                .param("dateTransactionStart", "2024-01-01")
                .param("dateTransactionEnd", "2024-12-31")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No accounts found for client id: 999"));
    }
}
