package com.expensetracker.app.dto;

import java.math.BigDecimal;

public class GoalRequest {
    private String userId;
    private String title;
    private String description;
    private BigDecimal targetAmount;
    private String startDate; // MM/dd/yyyy
    private String endDate;   // MM/dd/yyyy
    private String category;
    private String mode; // LIMIT or INVESTMENT

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
}