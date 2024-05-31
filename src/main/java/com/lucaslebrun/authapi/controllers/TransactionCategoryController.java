package com.lucaslebrun.authapi.controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.lucaslebrun.authapi.dtos.TransactionCategoryDto;
import com.lucaslebrun.authapi.dtos.TransactionCategoryOverviewDto;
import com.lucaslebrun.authapi.dtos.UserDto;
import com.lucaslebrun.authapi.entities.Transaction;
import com.lucaslebrun.authapi.entities.TransactionCategory;
import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.entities.UserGroup;
import com.lucaslebrun.authapi.services.TransactionCategoryService;
import com.lucaslebrun.authapi.services.TransactionService;
import com.lucaslebrun.authapi.services.AuthenticationService;
import com.lucaslebrun.authapi.services.UserGroupService;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/transactioncategories")
public class TransactionCategoryController {

    private final TransactionCategoryService transactionCategoryService;
    private final AuthenticationService authenticationService;
    private final UserGroupService UserGroupService;
    private TransactionService transactionService;

    public TransactionCategoryController(TransactionCategoryService transactionCategoryService,
            AuthenticationService authenticationService, UserGroupService UserGroupService,
            TransactionService transactionService) {
        this.transactionCategoryService = transactionCategoryService;
        this.authenticationService = authenticationService;
        this.UserGroupService = UserGroupService;
        this.transactionService = transactionService;
    }

    @PostMapping("")
    public ResponseEntity<TransactionCategoryDto> createTransactionCategory(
            @RequestBody TransactionCategoryDto TransactionCategoryDto) {
        User currentUser = authenticationService.getCurrentUser();

        UserGroup usergroup = UserGroupService.findGroupById(TransactionCategoryDto.getUserGroupId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserGroup not found"));

        if (!currentUser.getId().equals(usergroup.getAdmin().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        TransactionCategory transactionCategory = new TransactionCategory();
        transactionCategory.setName(TransactionCategoryDto.getName());
        transactionCategory.setUserGroup(usergroup);
        transactionCategory.setType(TransactionCategoryDto.getType());
        if (TransactionCategoryDto.getBudget() != null) {
            transactionCategory.setBudget(TransactionCategoryDto.getBudget());
        }

        transactionCategoryService.save(transactionCategory);

        return ResponseEntity.ok(new TransactionCategoryDto().setId(transactionCategory.getId())
                .setName(transactionCategory.getName()).setUserGroupId(transactionCategory.getUserGroup().getId())
                .setType(transactionCategory.getType()).setBudget(transactionCategory.getBudget()));

    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionCategoryDto> editTransactionCategory(@PathVariable Integer id,
            @RequestBody TransactionCategoryDto TransactionCategoryDto) {
        User currentUser = authenticationService.getCurrentUser();

        TransactionCategory transactionCategory = transactionCategoryService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction Category not found"));

        if (!currentUser.getId().equals(transactionCategory.getUserGroup().getAdmin().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        transactionCategory.setName(TransactionCategoryDto.getName());
        transactionCategory.setType(TransactionCategoryDto.getType());
        if (TransactionCategoryDto.getBudget() != null) {
            transactionCategory.setBudget(TransactionCategoryDto.getBudget());
        }
        transactionCategoryService.save(transactionCategory);

        return ResponseEntity.ok(new TransactionCategoryDto().setId(transactionCategory.getId())
                .setName(transactionCategory.getName()).setUserGroupId(transactionCategory.getUserGroup().getId())
                .setType(transactionCategory.getType()).setBudget(transactionCategory.getBudget()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionCategory(@PathVariable Integer id) {
        User currentUser = authenticationService.getCurrentUser();

        TransactionCategory transactionCategory = transactionCategoryService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction Category not found"));

        if (!currentUser.getId().equals(transactionCategory.getUserGroup().getAdmin().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // TODO: delete all transactions associated with this category

        transactionCategoryService.deleteById(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<List<TransactionCategoryDto>> getTransactionCategories() {
        User currentUser = authenticationService.getCurrentUser();

        List<TransactionCategory> transactionCategories = transactionCategoryService
                .findByUserGroups(currentUser.getGroups()).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User has no groups"));

        if (transactionCategories.isEmpty()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(transactionCategories.stream().map(transactionCategory -> new TransactionCategoryDto()
                .setId(transactionCategory.getId()).setName(transactionCategory.getName())
                .setUserGroupId(transactionCategory.getUserGroup().getId())
                .setType(transactionCategory.getType()).setBudget(transactionCategory.getBudget()))
                .collect(Collectors.toList()));

    }

    @GetMapping("/usergroup/{id}")
    public ResponseEntity<List<TransactionCategoryDto>> getTransactionCategoriesByUserGroup(@PathVariable Integer id) {
        User currentUser = authenticationService.getCurrentUser();

        UserGroup usergroup = UserGroupService.findGroupById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserGroup not found"));

        List<UserDto> memberDtos = usergroup.getMembers().stream()
                .map(member -> {
                    UserDto userDto = new UserDto(member);
                    return userDto;
                })
                .collect(Collectors.toList());

        System.out.println(memberDtos);

        boolean isMember = memberDtos.stream().anyMatch(member -> member.getId().equals(currentUser.getId()));

        if (!isMember) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<TransactionCategory> transactionCategories = transactionCategoryService.findByUserGroups(Set.of(usergroup))
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction Categories not found"));

        if (transactionCategories.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.ok(transactionCategories.stream().map(transactionCategory -> new TransactionCategoryDto()
                .setId(transactionCategory.getId()).setName(transactionCategory.getName())
                .setUserGroupId(transactionCategory.getUserGroup().getId())
                .setType(transactionCategory.getType()).setBudget(transactionCategory.getBudget()))
                .collect(Collectors.toList()));
    }

    @GetMapping("/usergroup/{id}/overview")
    public ResponseEntity<List<TransactionCategoryOverviewDto>> getTransactionCategoriesByUserGroupWithOverview(
            @PathVariable Integer id) {
        User currentUser = authenticationService.getCurrentUser();

        UserGroup usergroup = UserGroupService.findGroupById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserGroup not found"));

        List<UserDto> memberDtos = usergroup.getMembers().stream()
                .map(member -> {
                    UserDto userDto = new UserDto(member);
                    return userDto;
                })
                .collect(Collectors.toList());

        System.out.println(memberDtos);

        boolean isMember = memberDtos.stream().anyMatch(member -> member.getId().equals(currentUser.getId()));

        if (!isMember) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<TransactionCategory> transactionCategories = transactionCategoryService.findByUserGroups(Set.of(usergroup))
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction Categories not found"));

        if (transactionCategories.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.ok(transactionCategories.stream().map(transactionCategory -> {
            TransactionCategoryOverviewDto dto = new TransactionCategoryOverviewDto()
                    .setId(transactionCategory.getId()).setName(transactionCategory.getName())
                    .setUserGroupId(transactionCategory.getUserGroup().getId())
                    .setType(transactionCategory.getType()).setBudget(transactionCategory.getBudget());

            List<Transaction> transactions = transactionService.findByTransactionCategory(transactionCategory);

            BigDecimal totalTransactions = transactions.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            dto.setTracked(totalTransactions);

            if (transactionCategory.getBudget() != null) {
                dto.setCompletion(totalTransactions.divide(transactionCategory.getBudget(), 2, RoundingMode.HALF_UP));
                dto.setRemaining((transactionCategory.getBudget().subtract(totalTransactions)).max(BigDecimal.ZERO));
                dto.setExcess(totalTransactions.subtract(transactionCategory.getBudget()).max(BigDecimal.ZERO));
            }

            return dto;
        }).collect(Collectors.toList()));
    }

}
