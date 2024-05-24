package com.lucaslebrun.authapi.services;

import org.springframework.stereotype.Service;

import com.lucaslebrun.authapi.entities.Transaction;
import com.lucaslebrun.authapi.entities.TransactionCategory;
import com.lucaslebrun.authapi.repositories.TransactionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void deleteById(Integer id) {
        transactionRepository.deleteById(id);
    }

    public Transaction save(Transaction transaction) {
        transactionRepository.save(transaction);
        return transaction;
    }

    public void delete(Transaction transaction) {
        transactionRepository.delete(transaction);
    }

    public Optional<Transaction> findById(Integer id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> findByTransactionCategory(TransactionCategory transactionCategory) {
        return transactionRepository.findByTransactionCategory(transactionCategory);
    }

}
