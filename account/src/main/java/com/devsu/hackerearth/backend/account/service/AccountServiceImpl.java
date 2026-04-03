package com.devsu.hackerearth.backend.account.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.devsu.hackerearth.backend.account.model.Account;
import com.devsu.hackerearth.backend.account.model.dto.AccountDto;
import com.devsu.hackerearth.backend.account.model.dto.PartialAccountDto;
import com.devsu.hackerearth.backend.account.repository.AccountRepository;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<AccountDto> getAll() {
        return accountRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDto getById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return toDto(account);
    }

    @Override
    public AccountDto create(AccountDto accountDto) {
        Account account = toEntity(accountDto);
        account.setId(null);
        // Initialize current balance from initial amount when creating a new account
        account.setCurrentBalance(account.getInitialAmount());
        Account saved = accountRepository.save(account);
        return toDto(saved);
    }

    @Override
    public AccountDto update(AccountDto accountDto) {
        if (accountDto.getId() == null) {
            throw new RuntimeException("Account id is required for update");
        }
        Account existing = accountRepository.findById(accountDto.getId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        existing.setNumber(accountDto.getNumber());
        existing.setType(accountDto.getType());
        existing.setInitialAmount(accountDto.getInitialAmount());
        existing.setActive(Boolean.TRUE.equals(accountDto.getIsActive()));
        existing.setClientId(accountDto.getClientId());
        Account saved = accountRepository.save(existing);
        return toDto(saved);
    }

    @Override
    public AccountDto partialUpdate(Long id, PartialAccountDto partialAccountDto) {
        Account existing = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        existing.setActive(Boolean.TRUE.equals(partialAccountDto.getIsActive()));
        Account saved = accountRepository.save(existing);
        return toDto(saved);
    }

    @Override
    public void deleteById(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new RuntimeException("Account not found");
        }
        accountRepository.deleteById(id);
    }

    private AccountDto toDto(Account account) {
        if (account == null) {
            return null;
        }
        return new AccountDto(
                account.getId(),
                account.getNumber(),
                account.getType(),
                account.getInitialAmount(),
                account.isActive(),
                account.getClientId());
    }

    private Account toEntity(AccountDto dto) {
        if (dto == null) {
            return null;
        }
        Account account = new Account();
        account.setId(dto.getId());
        account.setNumber(dto.getNumber());
        account.setType(dto.getType());
        account.setInitialAmount(dto.getInitialAmount());
        account.setActive(Boolean.TRUE.equals(dto.getIsActive()));
        account.setClientId(dto.getClientId());
        return account;
    }
}
