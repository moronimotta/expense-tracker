package com.expensetracker.app.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final String CREDENTIALS_FILE = "firebase-service-account.json";

    @PostConstruct
    public void initialize() {
        try {
            System.out.println("Initializing Firebase...");
            
            if (FirebaseApp.getApps().isEmpty()) {
                // Try to load credentials from resources
                ClassPathResource resource = new ClassPathResource(CREDENTIALS_FILE);
                
                if (resource.exists()) {
                    try (InputStream serviceAccount = resource.getInputStream()) {
                        FirebaseOptions options = FirebaseOptions.builder()
                                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                                .build();

                        FirebaseApp.initializeApp(options);
                        System.out.println("Firebase initialized successfully with service account!");
                    }
                } else {
                    System.err.println("Firebase credentials file not found: " + CREDENTIALS_FILE);
                    System.err.println("Please add " + CREDENTIALS_FILE + " to src/main/resources/");
                    System.err.println("Application will use in-memory storage instead.");
                }
            } else {
                System.out.println("Firebase already initialized.");
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize Firebase: " + e.getMessage());
            System.err.println("Application will use in-memory storage instead.");
        } catch (Exception e) {
            System.err.println("Unexpected error initializing Firebase: " + e.getMessage());
            e.printStackTrace();
            System.err.println("Application will use in-memory storage instead.");
        }
    }
}