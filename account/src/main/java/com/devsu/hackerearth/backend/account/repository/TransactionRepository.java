package com.devsu.hackerearth.backend.account.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsu.hackerearth.backend.account.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findByAccountIdOrderByDateAsc(Long accountId);

	List<Transaction> findByAccountIdAndDateBetweenOrderByDateAsc(Long accountId, Date dateTransactionStart,
			Date dateTransactionEnd);

	Optional<Transaction> findTopByAccountIdOrderByDateDesc(Long accountId);
}
