package com.expensetracker.app.models.enums;

public enum GoalStatus {
    ACTIVE,
    UNDER_LIMIT,  // for LIMIT mode when spending <= target
    EXCEEDED,     // for LIMIT mode when spending > target
    SURPASSED     // for INVESTMENT mode when spending >= target
}