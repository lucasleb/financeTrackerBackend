package com.lucaslebrun.authapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucaslebrun.authapi.entities.Transaction;
import com.lucaslebrun.authapi.entities.TransactionCategory;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByTransactionCategory(TransactionCategory transactionCategory);

}
