package com.devsu.hackerearth.backend.client.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.devsu.hackerearth.backend.client.model.Client;
import com.devsu.hackerearth.backend.client.model.dto.ClientDto;
import com.devsu.hackerearth.backend.client.model.dto.PartialClientDto;
import com.devsu.hackerearth.backend.client.repository.ClientRepository;

@Service
public class ClientServiceImpl implements ClientService {

	private final ClientRepository clientRepository;

	public ClientServiceImpl(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	@Override
	public List<ClientDto> getAll() {
		return clientRepository.findAll().stream()
				.map(this::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public ClientDto getById(Long id) {
		Client client = clientRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Client not found"));
		return toDto(client);
	}

	@Override
	public ClientDto create(ClientDto clientDto) {
		Client client = toEntity(clientDto);
		client.setId(null);
		Client saved = clientRepository.save(client);
		return toDto(saved);
	}

	@Override
	public ClientDto update(ClientDto clientDto) {
		if (clientDto.getId() == null) {
			throw new RuntimeException("Client id is required for update");
		}
		Client existing = clientRepository.findById(clientDto.getId())
				.orElseThrow(() -> new RuntimeException("Client not found"));
		existing.setName(clientDto.getName());
		existing.setDni(clientDto.getDni());
		existing.setGender(clientDto.getGender());
		existing.setAge(clientDto.getAge());
		existing.setAddress(clientDto.getAddress());
		existing.setPhone(clientDto.getPhone());
		existing.setPassword(clientDto.getPassword());
		existing.setActive(Boolean.TRUE.equals(clientDto.getIsActive()));
		Client saved = clientRepository.save(existing);
		return toDto(saved);
	}

	@Override
	public ClientDto partialUpdate(Long id, PartialClientDto partialClientDto) {
		Client existing = clientRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Client not found"));
		existing.setActive(Boolean.TRUE.equals(partialClientDto.getIsActive()));
		Client saved = clientRepository.save(existing);
		return toDto(saved);
	}

	@Override
	public void deleteById(Long id) {
		if (!clientRepository.existsById(id)) {
			throw new RuntimeException("Client not found");
		}
		clientRepository.deleteById(id);
	}

	private ClientDto toDto(Client client) {
		if (client == null) {
			return null;
		}
		return new ClientDto(
				client.getId(),
				client.getDni(),
				client.getName(),
				client.getPassword(),
				client.getGender(),
				client.getAge(),
				client.getAddress(),
				client.getPhone(),
				client.isActive());
	}

	private Client toEntity(ClientDto dto) {
		if (dto == null) {
			return null;
		}
		Client client = new Client();
		client.setId(dto.getId());
		client.setDni(dto.getDni());
		client.setName(dto.getName());
		client.setPassword(dto.getPassword());
		client.setGender(dto.getGender());
		client.setAge(dto.getAge());
		client.setAddress(dto.getAddress());
		client.setPhone(dto.getPhone());
		client.setActive(Boolean.TRUE.equals(dto.getIsActive()));
		return client;
	}
}
