package com.lucaslebrun.authapi.services;

import com.lucaslebrun.authapi.entities.Transaction;
import com.lucaslebrun.authapi.entities.TransactionCategory;
import com.lucaslebrun.authapi.entities.TransactionCategory.TransactionType;
import com.lucaslebrun.authapi.entities.User;
import com.lucaslebrun.authapi.entities.UserGroup;
import com.lucaslebrun.authapi.entities.UserGroupInvitation;
import com.lucaslebrun.authapi.repositories.TransactionCategoryRepository;
import com.lucaslebrun.authapi.repositories.TransactionRepository;
import com.lucaslebrun.authapi.repositories.UserGroupInvitationRepository;
import com.lucaslebrun.authapi.repositories.UserGroupRepository;
import com.lucaslebrun.authapi.repositories.UserRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DemoService {

    private final UserGroupRepository userGroupRepository;

    private final UserRepository userRepository;

    private final TransactionCategoryRepository transactionCategoryRepository;

    private final TransactionRepository transactionRepository;

    private final UserGroupInvitationRepository userGroupInvitationRepository;

    public DemoService(UserGroupRepository userGroupRepository, UserRepository userRepository,
            TransactionCategoryRepository transactionCategoryRepository, TransactionRepository transactionRepository,
            UserGroupInvitationRepository userGroupInvitationRepository) {
        this.userGroupRepository = userGroupRepository;
        this.userRepository = userRepository;
        this.transactionCategoryRepository = transactionCategoryRepository;
        this.transactionRepository = transactionRepository;
        this.userGroupInvitationRepository = userGroupInvitationRepository;
    }

    public void initializeDemoSetup() {

        System.out.println("Initializing demo setup...");

        // delete the groups associted with demoUser and demoUSer2(taht will also delte
        // the categories and transactions)

        // find users by email and delete
        User demoUser = userRepository.findByEmail("demo@user.com").get();
        User demoUser2 = userRepository.findByEmail("demo@user2.com").get();

        System.out.println("Demo user: " + demoUser);
        System.out.println("Demo user2: " + demoUser2);

        userGroupInvitationRepository.findByAuthor(demoUser).forEach(userGroupInvitation -> {
            userGroupInvitationRepository.delete(userGroupInvitation);
        });
        userGroupInvitationRepository.findByAuthor(demoUser2).forEach(userGroupInvitation -> {
            userGroupInvitationRepository.delete(userGroupInvitation);
        });
        System.out.println("Deleted demo user invitations...");

        demoUser.getGroups().forEach(userGroup -> {
            transactionCategoryRepository.findByUserGroup(userGroup).forEach(transactionCategory -> {
                transactionRepository.findByTransactionCategory(transactionCategory).forEach(transaction -> {
                    transactionRepository.delete(transaction);
                });
                transactionCategoryRepository.delete(transactionCategory);
            });
            userGroupRepository.delete(userGroup);
        });
        demoUser2.getGroups().forEach(userGroup -> {
            transactionCategoryRepository.findByUserGroup(userGroup).forEach(transactionCategory -> {
                transactionRepository.findByTransactionCategory(transactionCategory).forEach(transaction -> {
                    transactionRepository.delete(transaction);
                });
                transactionCategoryRepository.delete(transactionCategory);
            });
            userGroupRepository.delete(userGroup);
        });

        System.out.println("Deleted demo groups and associated categories and transactions...");

        // print out groups assocaited with the users just to make sure they are deleted
        System.out.println("Demo user groups: " + userGroupRepository.findByAdmin(demoUser));
        System.out.println("Demo user2 groups: " + userGroupRepository.findByAdmin(demoUser2));

        // Create a group for demoUser and for demoUser2
        UserGroup userGroup = new UserGroup("My personnal finances", demoUser);
        UserGroup userGroup2 = new UserGroup("Household finances", demoUser);
        UserGroup userGroup3 = new UserGroup("Startup", demoUser2);
        userGroup2.addMember(demoUser2);
        userGroupRepository.save(userGroup);
        userGroupRepository.save(userGroup2);
        userGroupRepository.save(userGroup3);

        System.out.println("Created demo groups...");
        System.out.println("Demo user groups: " + userGroupRepository.findByAdmin(demoUser));
        System.out.println("Demo user2 groups: " + userGroupRepository.findByAdmin(demoUser2));

        // Create invitation for demoUser to join demoUser2's group
        UserGroupInvitation userGroupInvitation = new UserGroupInvitation(demoUser2, demoUser, userGroup3);
        userGroupInvitationRepository.save(userGroupInvitation);

        System.out.println("Created invitation for demoUser to join demoUser2's group...");
        System.out.println("Demo user2 group invitations: " + userGroupInvitationRepository.findByAuthor(demoUser2));

        double householdIncomes = 40000 + (Math.random() * 60000);
        double personalIncomes = householdIncomes / 10.00;

        double householdSavingsRate = -0.10 + (Math.random() * 0.40);
        double personalSavingsRate = -0.10 + (Math.random() * 0.40);

        double householdSavings = householdIncomes * householdSavingsRate;
        double personalSavings = personalIncomes * personalSavingsRate;

        double householdExpenses = householdIncomes - householdSavings;
        double personalExpenses = personalIncomes - personalSavings;

        Map<String, BigDecimal> householdExpenseCategories = new HashMap<>();
        householdExpenseCategories.put("Rent", defineGoal(householdExpenses, 0.4, 0.10));
        householdExpenseCategories.put("Utilities", defineGoal(householdExpenses, 0.05, 0.03));
        householdExpenseCategories.put("Groceries", defineGoal(householdExpenses, 0.2, 0.10));
        householdExpenseCategories.put("Transportation", defineGoal(householdExpenses, 0.1, 0.07));
        householdExpenseCategories.put("Healthcare", defineGoal(householdExpenses, 0.05, 0.03));
        householdExpenseCategories.put("Insurance", defineGoal(householdExpenses, 0.05, 0.03));
        householdExpenseCategories.put("Travel", defineGoal(householdExpenses, 0.05, 0.03));
        householdExpenseCategories.put("Bar & Restaurants", defineGoal(householdExpenses, 0.05, 0.03));
        householdExpenseCategories.put("Dining Out", defineGoal(householdExpenses, 0.05, 0.03));
        householdExpenseCategories.put("Miscellaneous", defineGoal(householdExpenses, 0.05, 0.03));

        Map<String, BigDecimal> personalExpenseCategories = new HashMap<>();
        personalExpenseCategories.put("Personal Care", defineGoal(personalExpenses, 0.2, 0.05));
        personalExpenseCategories.put("Clothing", defineGoal(personalExpenses, 0.2, 0.05));
        personalExpenseCategories.put("Entertainment", defineGoal(personalExpenses, 0.2, 0.05));
        personalExpenseCategories.put("Gifts and Donations", defineGoal(personalExpenses, 0.2, 0.05));
        personalExpenseCategories.put("Miscellaneous", defineGoal(personalExpenses, 0.2, 0.05));

        double balance = 0.35 + (Math.random() * 0.30);

        Map<String, BigDecimal> householdIncomeCategories = new HashMap<>();
        householdIncomeCategories.put("John's Employement", defineGoal(householdIncomes, balance, 0.));
        householdIncomeCategories.put("Jane's Employement", defineGoal(householdIncomes, 1.0 - balance, 0.));

        Map<String, BigDecimal> personalIncomeCategories = new HashMap<>();
        personalIncomeCategories.put("Personal Allowance", new BigDecimal(Math.round(personalIncomes)));
        personalIncomeCategories.put("Side Hustle", defineGoal(personalIncomes, 0.2, 0.10));

        System.out.println("Creating categories...");
        System.out.println("Household expense categories: " + householdExpenseCategories);
        System.out.println("Personal expense categories: " + personalExpenseCategories);
        System.out.println("Household income categories: " + householdIncomeCategories);
        System.out.println("Personal income categories: " + personalIncomeCategories);

        // loop through the categories and create them
        householdExpenseCategories.forEach((name, budget) -> {
            TransactionType type = TransactionType.EXPENSE;
            TransactionCategory category = new TransactionCategory(name, userGroup2, type, budget);
            transactionCategoryRepository.save(category);
        });

        personalExpenseCategories.forEach((name, budget) -> {
            TransactionType type = TransactionType.EXPENSE;
            TransactionCategory category = new TransactionCategory(name, userGroup, type, budget);
            transactionCategoryRepository.save(category);
        });

        householdIncomeCategories.forEach((name, budget) -> {
            TransactionType type = TransactionType.INCOME;
            TransactionCategory category = new TransactionCategory(name, userGroup2, type, budget);
            transactionCategoryRepository.save(category);
        });

        personalIncomeCategories.forEach((name, budget) -> {
            TransactionType type = TransactionType.INCOME;
            TransactionCategory category = new TransactionCategory(name, userGroup, type, budget);
            transactionCategoryRepository.save(category);
        });

        Integer date = (int) (100 + ((Math.random()) * 250));

        Double fractionYear = date / 365.0;

        transactionCategoryRepository.findByUserGroup(userGroup).forEach(transactionCategory -> {
            BigDecimal budget = transactionCategory.getBudget();
            BigDecimal proportionnalBudget = budget.multiply(new BigDecimal(fractionYear));
            // if transactionCateogry.type == TransactionType.EXPENSE, multiply
            // proportionnalBudget by a random number between 0.5 and 1.5
            if (transactionCategory.getType() == TransactionType.EXPENSE) {
                proportionnalBudget = proportionnalBudget.multiply(new BigDecimal(0.5 + Math.random()));
            }
            while (proportionnalBudget.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal transactionAmount = new BigDecimal(
                        Math.round(10. + Math.random() * 100.));
                Date randomDate = Date
                        .valueOf(LocalDate.now().withDayOfYear(1).plusDays(ThreadLocalRandom.current().nextInt(date)));
                Transaction transaction = new Transaction("Randomly generated transaction",
                        transactionCategory, randomDate,
                        transactionAmount);
                transactionRepository.save(transaction);
                proportionnalBudget = proportionnalBudget.subtract(transactionAmount);
            }
        });

        transactionCategoryRepository.findByUserGroup(userGroup2).forEach(transactionCategory -> {
            BigDecimal budget = transactionCategory.getBudget();
            BigDecimal proportionnalBudget = budget.multiply(new BigDecimal(fractionYear));
            while (proportionnalBudget.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal transactionAmount = new BigDecimal(
                        Math.round(10. + Math.random() * 100.));
                Date randomDate = Date
                        .valueOf(LocalDate.now().withDayOfYear(1).plusDays(ThreadLocalRandom.current().nextInt(date)));
                Transaction transaction = new Transaction("Randomly generated transaction",
                        transactionCategory, randomDate,
                        transactionAmount);
                transactionRepository.save(transaction);
                proportionnalBudget = proportionnalBudget.subtract(transactionAmount);
            }
        });

    }

    private BigDecimal defineGoal(double totalTransactions, double average, double random) {
        return new BigDecimal(
                Math.round((totalTransactions * (average + ((Math.random() - 0.5) * random))) / 100.0) * 100.0);
    }

}
