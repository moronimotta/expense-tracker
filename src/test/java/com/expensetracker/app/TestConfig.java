package com.expensetracker.app;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import com.expensetracker.app.repositories.UserRepository;
import com.expensetracker.app.repositories.ExpenseRepository;
import com.expensetracker.app.repositories.GoalRepository;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public UserRepository testUserRepository() {
        return new UserRepository();
    }

    @Bean
    @Primary
    public ExpenseRepository testExpenseRepository() {
        return new ExpenseRepository();
    }

    @Bean
    @Primary
    public GoalRepository testGoalRepository(ExpenseRepository expenseRepository) {
        return new GoalRepository(expenseRepository);
    }
}