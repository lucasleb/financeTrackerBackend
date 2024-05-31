package com.lucaslebrun.authapi.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.lucaslebrun.authapi.dtos.TransactionDto;
import com.lucaslebrun.authapi.dtos.UserDto;
import com.lucaslebrun.authapi.entities.Transaction;
import com.lucaslebrun.authapi.entities.TransactionCategory;
import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.entities.UserGroup;
import com.lucaslebrun.authapi.services.AuthenticationService;
import com.lucaslebrun.authapi.services.TransactionCategoryService;
import com.lucaslebrun.authapi.services.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final AuthenticationService authenticationService;
    private final TransactionCategoryService transactionCategoryService;
    private final TransactionService transactionService;

    public TransactionController(AuthenticationService authenticationService,
            TransactionCategoryService transactionCategoryService, TransactionService transactionService) {
        this.authenticationService = authenticationService;
        this.transactionCategoryService = transactionCategoryService;
        this.transactionService = transactionService;
    }

    @PostMapping("")
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto transactionDto) {
        User currentUser = authenticationService.getCurrentUser();

        TransactionCategory transactionCategory = transactionCategoryService.findById(transactionDto.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction Category not found"));

        UserGroup userGroup = transactionCategory.getUserGroup();

        // TODO: find simpler way to find if user is a member of the group
        List<UserDto> memberDtos = userGroup.getMembers().stream()
                .map(member -> {
                    UserDto userDto = new UserDto(member);
                    return userDto;
                })
                .collect(Collectors.toList());

        boolean isMember = memberDtos.stream().anyMatch(member -> member.getId().equals(currentUser.getId()));

        if (!isMember) {
            return ResponseEntity.status(403).build();
        }

        Transaction transaction = new Transaction(transactionDto.getDescription(), transactionCategory,
                transactionDto.getDate(), transactionDto.getAmount());

        transactionService.save(transaction);

        return ResponseEntity
                .ok(new TransactionDto().setId(transaction.getId()).setDescription(transaction.getDescription())
                        .setDate(transaction.getDate())
                        .setAmount(transaction.getAmount())
                        .setCategoryId(transaction.getTransactionCategory().getId()));

    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDto> editTransaction(@PathVariable Integer id,
            @RequestBody TransactionDto transactionDto) {
        User currentUser = authenticationService.getCurrentUser();

        Transaction transaction = transactionService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        TransactionCategory transactionCategory = transaction.getTransactionCategory();

        UserGroup userGroup = transactionCategory.getUserGroup();

        // TODO: find simpler way to find if user is a member of the group
        List<UserDto> memberDtos = userGroup.getMembers().stream()
                .map(member -> {
                    UserDto userDto = new UserDto(member);
                    return userDto;
                })
                .collect(Collectors.toList());

        boolean isMember = memberDtos.stream().anyMatch(member -> member.getId().equals(currentUser.getId()));

        if (!isMember) {
            return ResponseEntity.status(403).build();
        }

        transaction.setDescription(transactionDto.getDescription());
        transaction.setDate(transactionDto.getDate());
        transaction.setAmount(transactionDto.getAmount());

        transactionService.save(transaction);

        return ResponseEntity
                .ok(new TransactionDto().setId(transaction.getId()).setDescription(transaction.getDescription())
                        .setDate(transaction.getDate())
                        .setAmount(transaction.getAmount())
                        .setCategoryId(transaction.getTransactionCategory().getId()));

    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransaction(@PathVariable Integer id) {
        User currentUser = authenticationService.getCurrentUser();

        Transaction transaction = transactionService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        TransactionCategory transactionCategory = transaction.getTransactionCategory();

        UserGroup userGroup = transactionCategory.getUserGroup();

        // TODO: find simpler way to find if user is a member of the group
        List<UserDto> memberDtos = userGroup.getMembers().stream()
                .map(member -> {
                    UserDto userDto = new UserDto(member);
                    return userDto;
                })
                .collect(Collectors.toList());

        boolean isMember = memberDtos.stream().anyMatch(member -> member.getId().equals(currentUser.getId()));

        if (!isMember) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity
                .ok(new TransactionDto().setId(transaction.getId()).setDescription(transaction.getDescription())
                        .setDate(transaction.getDate())
                        .setAmount(transaction.getAmount())
                        .setCategoryId(transaction.getTransactionCategory().getId()));

    }

    @GetMapping("/category/{id}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByCategory(@PathVariable Integer id) {
        User currentUser = authenticationService.getCurrentUser();

        TransactionCategory transactionCategory = transactionCategoryService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction Category not found"));

        UserGroup userGroup = transactionCategory.getUserGroup();

        // TODO: find simpler way to find if user is a member of the group
        List<UserDto> memberDtos = userGroup.getMembers().stream()
                .map(member -> {
                    UserDto userDto = new UserDto(member);
                    return userDto;
                })
                .collect(Collectors.toList());

        boolean isMember = memberDtos.stream().anyMatch(member -> member.getId().equals(currentUser.getId()));

        if (!isMember) {
            return ResponseEntity.status(403).build();
        }

        List<Transaction> transactions = transactionService.findByTransactionCategory(transactionCategory);

        if (transactions.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.ok(transactions.stream().map(transaction -> new TransactionDto()
                .setId(transaction.getId()).setDescription(transaction.getDescription())
                .setDate(transaction.getDate())
                .setAmount(transaction.getAmount())
                .setCategoryId(transaction.getTransactionCategory().getId()))
                .collect(Collectors.toList()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Integer id) {
        User currentUser = authenticationService.getCurrentUser();

        Transaction transaction = transactionService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        TransactionCategory transactionCategory = transaction.getTransactionCategory();

        UserGroup userGroup = transactionCategory.getUserGroup();

        // TODO: find simpler way to find if user is a member of the group
        List<UserDto> memberDtos = userGroup.getMembers().stream()
                .map(member -> {
                    UserDto userDto = new UserDto(member);
                    return userDto;
                })
                .collect(Collectors.toList());

        boolean isMember = memberDtos.stream().anyMatch(member -> member.getId().equals(currentUser.getId()));

        if (!isMember) {
            return ResponseEntity.status(403).build();
        }

        transactionService.deleteById(id);

        return ResponseEntity.ok().build();

    }

@GetMapping
    public ResponseEntity<List<TransactionDto>> getTransactions(
            @RequestParam(required = false) Integer categoryId) {
        TransactionCategory transactionCategory = transactionCategoryService.findById(categoryId);
        List<Transaction> transactions = transactionService.findByTransactionCategory(transactionCategory);

    List<TransactionDto> transactionDtos = transactions.stream()
        .map(transaction -> new TransactionDto().setId(transaction.getId())
            .setDescription(transaction.getDescription()).setDate(transaction.getDate())
            .setAmount(transaction.getAmount())
            .setCategoryId(transaction.getTransactionCategory().getId()))
        return ResponseEntity.ok(transactionDtos);
    }


}
