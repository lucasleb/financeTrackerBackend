package com.lucaslebrun.authapi.dtos;

import java.math.BigDecimal;

public class TransactionDto {

    private Integer id;

    private Integer categoryId;

    private String description;

    private java.sql.Date date;

    private BigDecimal amount;

    public TransactionDto() {
    }

    public TransactionDto(Integer categoryId, String description, java.sql.Date date,
            BigDecimal amount) {
        this.categoryId = categoryId;
        this.description = description;
        this.date = date;
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public TransactionDto setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public TransactionDto setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TransactionDto setDescription(String description) {
        this.description = description;
        return this;
    }

    public java.sql.Date getDate() {
        return date;
    }

    public TransactionDto setDate(java.sql.Date date) {
        this.date = date;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionDto setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

}
