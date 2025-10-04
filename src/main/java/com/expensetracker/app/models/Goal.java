package com.expensetracker.app.models;

import lombok.*;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.Exclude;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.expensetracker.app.config.jackson.TimestampSerializer;
import com.expensetracker.app.models.enums.GoalMode;
import com.expensetracker.app.models.enums.GoalStatus;
import com.expensetracker.app.config.jackson.TimestampDeserializer;
import com.expensetracker.app.models.enums.GoalMode;
import com.expensetracker.app.models.enums.GoalStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Represents a financial goal in the expense tracker system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
public class Goal extends BaseEntity {

    private String userId;
    private String title;
    private String description;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount = BigDecimal.ZERO;
    @JsonSerialize(using = TimestampSerializer.class)
    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp startDate; 
    @JsonSerialize(using = TimestampSerializer.class)
    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp endDate;   
    private String category;
    private boolean isCompleted = false;
    private GoalMode mode = GoalMode.LIMIT;
    private GoalStatus status = GoalStatus.ACTIVE;

    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getTargetAmount() {
        return targetAmount;
    }
    
    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }
    
    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }
    
    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }
    
    public Timestamp getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }
    
    public Timestamp getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    // Helper methods for LocalDateTime conversion (business logic)
    @Exclude
    public LocalDateTime getStartDateAsLocalDateTime() {
        return startDate != null ? 
            LocalDateTime.ofInstant(startDate.toDate().toInstant(), ZoneId.systemDefault()) : 
            null;
    }

    @Exclude
    public LocalDateTime getEndDateAsLocalDateTime() {
        return endDate != null ? 
            LocalDateTime.ofInstant(endDate.toDate().toInstant(), ZoneId.systemDefault()) : 
            null;
    }

    @Exclude
    public void setStartDateFromLocalDateTime(LocalDateTime localDateTime) {
        this.startDate = localDateTime != null ? 
            Timestamp.of(java.sql.Timestamp.valueOf(localDateTime)) : 
            null;
    }

    @Exclude
    public void setEndDateFromLocalDateTime(LocalDateTime localDateTime) {
        this.endDate = localDateTime != null ? 
            Timestamp.of(java.sql.Timestamp.valueOf(localDateTime)) : 
            null;
    }
    
    // Helper methods
    @Exclude
    public BigDecimal getProgress() {
        if (targetAmount == null || targetAmount.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        if (currentAmount == null) {
            return BigDecimal.ZERO;
        }
        return currentAmount.divide(targetAmount, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }
    
    @Exclude
    public BigDecimal getRemainingAmount() {
        if (targetAmount == null) {
            return BigDecimal.ZERO;
        }
        if (currentAmount == null) {
            return targetAmount;
        }
        return targetAmount.subtract(currentAmount);
    }
    
    @Override
    public String toString() {
        return "Goal{" +
                "id='" + getId() + '\'' +
                ", userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", targetAmount=" + targetAmount +
                ", currentAmount=" + currentAmount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", category='" + category + '\'' +
                ", isCompleted=" + isCompleted +
                '}';
    }
}