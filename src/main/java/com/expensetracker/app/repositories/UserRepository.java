package com.expensetracker.app.repositories;

import com.expensetracker.app.models.User;
import com.expensetracker.app.exceptions.DuplicateEmailException;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.Timestamp; 
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 
import java.util.concurrent.ExecutionException;

@Repository
public class UserRepository {

    private static final String COLLECTION_NAME = "users";
    private static final String DELETED_AT_FIELD = "deletedAt";

    public List<User> findAll() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        List<User> users = new ArrayList<>();

        // Only retrieve documents where deletedAt == null
        List<QueryDocumentSnapshot> documents = dbFirestore.collection(COLLECTION_NAME)
                .whereEqualTo(DELETED_AT_FIELD, null)
                .get()
                .get()
                .getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            User user = document.toObject(User.class);
            if (user != null) {
                user.setId(document.getId());
                users.add(user);
            }
        }

        return users;
    }

    public User findById(String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        User user = dbFirestore.collection(COLLECTION_NAME)
                .document(id)
                .get()
                .get()
                .toObject(User.class);

        if (user != null) {
            user.setId(id);
        }

        return user;
    }

    public User createUser(User user) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(java.util.UUID.randomUUID().toString());
        }

        List<QueryDocumentSnapshot> documents = dbFirestore.collection(COLLECTION_NAME)
                .whereEqualTo("email", user.getEmail())
                .get()
                .get()
                .getDocuments();
        if (!documents.isEmpty()) {
            throw new DuplicateEmailException("Email already exists");
        }

        dbFirestore.collection(COLLECTION_NAME)
                .document(user.getId())
                .set(user)
                .get();

        return user;
    }

     public User update(User user) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        if (user.getId() == null || user.getId().isEmpty()) {
            throw new IllegalArgumentException("User ID is required for update");
        }

        if (user.getEmail() != null) {
            List<QueryDocumentSnapshot> emailDocs = dbFirestore.collection(COLLECTION_NAME)
                    .whereEqualTo("email", user.getEmail())
                    .get()
                    .get()
                    .getDocuments();

            for (QueryDocumentSnapshot doc : emailDocs) {
                if (!doc.getId().equals(user.getId())) {
                    throw new DuplicateEmailException("Email already exists");
                }
            }
        }

        Map<String, Object> updates = new HashMap<>();

        if (user.getName() != null) updates.put("name", user.getName());
        if (user.getEmail() != null) updates.put("email", user.getEmail());
        if (user.getPassword() != null) updates.put("password", user.getPassword());
        if (user.getRole() != null) updates.put("role", user.getRole());
        if (user.getDeletedAt() != null) updates.put("deletedAt", user.getDeletedAt());

        updates.put("updatedAt", Timestamp.now());

        if (updates.size() == 1 && updates.containsKey("updatedAt")) {
            return findById(user.getId());
        }

        dbFirestore.collection(COLLECTION_NAME)
                .document(user.getId())
                .update(updates)
                .get();

        return findById(user.getId());
    }
    
    public void deleteById(String id) throws ExecutionException, InterruptedException {
        User user = findById(id);
        if (user != null) {
            user.softDelete();
            update(user);
        }
    }

}