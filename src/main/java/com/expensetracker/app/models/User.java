package com.expensetracker.app.models;

import com.expensetracker.app.models.enums.UserRole;
import lombok.*;

/**
 * Represents a user in the expense tracker system.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    private String name;
    private String email;
    private String password;
    private UserRole role = UserRole.USER; // Default role

    // Explicit constructor for Firestore
    public User(String name, String email, String password) {
        super();
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = UserRole.USER;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + getId() + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
}