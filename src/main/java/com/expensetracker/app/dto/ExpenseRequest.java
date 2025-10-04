package com.expensetracker.app.dto;

import java.math.BigDecimal;

public class ExpenseRequest {
    private String description;
    private BigDecimal amount;
    private String category;
    // ISO 8601 string, e.g. 2025-10-02T13:00:00Z
    private String date;
    private String userId;
    private String goalId; // optional link to a goal

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getGoalId() { return goalId; }
    public void setGoalId(String goalId) { this.goalId = goalId; }
}