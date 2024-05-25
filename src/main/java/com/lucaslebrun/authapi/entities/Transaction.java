package com.lucaslebrun.authapi.entities;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "transaction_category_id", nullable = false)
    private TransactionCategory transactionCategory;

    @Column(nullable = false)
    private java.sql.Date date;

    @Column(nullable = false)
    private BigDecimal amount;

    // getters and setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionCategory getTransactionCategory() {
        return transactionCategory;
    }

    public void setTransactionCategory(TransactionCategory transactionCategory) {
        this.transactionCategory = transactionCategory;
    }

    public java.sql.Date getDate() {
        return date;
    }

    public void setDate(java.sql.Date date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    // constructors

    public Transaction() {
    }

    public Transaction(String description, TransactionCategory transactionCategory, java.sql.Date date,
            BigDecimal amount) {
        this.description = description;
        this.transactionCategory = transactionCategory;
        this.date = date;
        this.amount = amount;
    }

    public Transaction(TransactionCategory transactionCategory, java.sql.Date date, BigDecimal amount) {
        this.transactionCategory = transactionCategory;
        this.date = date;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction [amount=" + amount + ", date=" + date + ", description=" + description + ", id=" + id
                + ", transactionCategory=" + transactionCategory + "]";
    }

}
