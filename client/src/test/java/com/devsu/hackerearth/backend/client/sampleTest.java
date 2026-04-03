package com.devsu.hackerearth.backend.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.devsu.hackerearth.backend.client.controller.ClientController;
import com.devsu.hackerearth.backend.client.model.dto.ClientDto;
import com.devsu.hackerearth.backend.client.service.ClientService;

public class sampleTest {

    private final ClientService clientService = mock(ClientService.class);
    private final ClientController clientController = new ClientController(clientService);

    @Test
    void createClientTest() {
        ClientDto newClient = new ClientDto(1L, "Dni", "Name", "Password", "Gender", 1, "Address", "9999999999", true);
        ClientDto createdClient = new ClientDto(1L, "Dni", "Name", "Password", "Gender", 1, "Address", "9999999999", true);
        when(clientService.create(newClient)).thenReturn(createdClient);

        ResponseEntity<ClientDto> response = clientController.create(newClient);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdClient, response.getBody());
    }

    @Test
    void deleteClientTest() {
        Long id = 1L;
        doNothing().when(clientService).deleteById(id);

        ResponseEntity<Void> response = clientController.delete(id);

        verify(clientService).deleteById(id);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
