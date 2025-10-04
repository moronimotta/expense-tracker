package com.expensetracker.app.models;

import jakarta.persistence.*;
import com.google.cloud.firestore.annotation.Exclude;
import com.google.cloud.Timestamp;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp updatedAt;

    @Column
    private Timestamp deletedAt;

    public BaseEntity() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = Timestamp.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = Timestamp.now();
        }
    }

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = Timestamp.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void softDelete() {
        this.deletedAt = Timestamp.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    // Helper methods to convert to/from Instant for business logic
    @Exclude
    public Instant getCreatedAtInstant() {
        return createdAt != null ? createdAt.toDate().toInstant() : null;
    }

    @Exclude
    public Instant getUpdatedAtInstant() {
        return updatedAt != null ? updatedAt.toDate().toInstant() : null;
    }

    @Exclude
    public Instant getDeletedAtInstant() {
        return deletedAt != null ? deletedAt.toDate().toInstant() : null;
    }
}
