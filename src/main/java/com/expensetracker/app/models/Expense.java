package com.expensetracker.app.models;

import lombok.*;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.Exclude;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import com.expensetracker.app.models.enums.ExpenseCategory;

/**
 * Represents an expense in the expense tracker system.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
public class Expense extends BaseEntity {

    private String description;
    private BigDecimal amount;
    private String category; // e.g., Food, Travel, Utilities, Housing
    private Timestamp date; 
    private String userId;
    private String goalId; 

    public Expense(String description, BigDecimal amount, String category, LocalDateTime date, String userId) {
        super();
        this.description = description;
        this.amount = amount;
        this.category = ExpenseCategory.from(category).name();
        this.date = date != null ? 
            Timestamp.of(java.sql.Timestamp.valueOf(date)) : 
            Timestamp.now();
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = ExpenseCategory.from(category).name();
    }
    
    public Timestamp getDate() {
        return date;
    }
    
    public void setDate(Timestamp date) {
        this.date = date;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Helper methods for LocalDateTime conversion 
    @Exclude
    public LocalDateTime getDateAsLocalDateTime() {
        return date != null ? 
            LocalDateTime.ofInstant(date.toDate().toInstant(), ZoneId.systemDefault()) : 
            null;
    }

    @Exclude
    public void setDateFromLocalDateTime(LocalDateTime localDateTime) {
        this.date = localDateTime != null ? 
            Timestamp.of(java.sql.Timestamp.valueOf(localDateTime)) : 
            null;
    }
    
    @Override
    public String toString() {
        return "Expense{" +
                "id='" + getId() + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", date=" + date +
                ", userId='" + userId + '\'' +
                '}';
    }
}