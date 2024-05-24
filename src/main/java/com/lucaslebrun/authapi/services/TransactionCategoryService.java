package com.lucaslebrun.authapi.services;

import org.springframework.stereotype.Service;

import com.lucaslebrun.authapi.entities.TransactionCategory;
import com.lucaslebrun.authapi.entities.UserGroup;
import com.lucaslebrun.authapi.repositories.TransactionCategoryRepository;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;

@Service
public class TransactionCategoryService {
    private final TransactionCategoryRepository transactionCategoryRepository;

    public TransactionCategoryService(TransactionCategoryRepository transactionCategoryRepository) {
        this.transactionCategoryRepository = transactionCategoryRepository;
    }

    public void deleteById(Integer id) {
        transactionCategoryRepository.deleteById(id);
    }

    public TransactionCategory save(TransactionCategory transactionCategory) {
        transactionCategoryRepository.save(transactionCategory);
        return transactionCategory;
    }

    public void delete(TransactionCategory transactionCategory) {
        transactionCategoryRepository.delete(transactionCategory);
    }

    public Optional<List<TransactionCategory>> findByUserGroups(Set<UserGroup> userGroups) {
        List<TransactionCategory> transactionCategories = userGroups.stream()
                .flatMap(userGroup -> transactionCategoryRepository.findByUserGroup(userGroup).stream())
                .collect(Collectors.toList());
        return Optional.ofNullable(transactionCategories.isEmpty() ? null : transactionCategories);
    }

    public Optional<TransactionCategory> findById(Integer id) {
        return transactionCategoryRepository.findById(id);
    }
}
