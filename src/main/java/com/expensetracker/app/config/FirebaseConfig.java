package com.expensetracker.app.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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
                // 1) Try GOOGLE_APPLICATION_CREDENTIALS env var
                String gac = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
                if (gac != null && !gac.isBlank()) {
                    try {
                        InputStream credsStream;
                        File f = new File(gac);
                        if (f.exists()) {
                            credsStream = new FileInputStream(f);
                            System.out.println("Initializing Firebase using credentials file at: " + f.getAbsolutePath());
                        } else {
                            // Treat as inline JSON
                            System.out.println("Initializing Firebase using inline credentials JSON from env var");
                            credsStream = new ByteArrayInputStream(gac.getBytes());
                        }

                        try (InputStream serviceAccount = credsStream) {
                            FirebaseOptions options = FirebaseOptions.builder()
                                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                                    .build();
                            FirebaseApp.initializeApp(options);
                            System.out.println("Firebase initialized successfully from GOOGLE_APPLICATION_CREDENTIALS");
                            return;
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to init Firebase from GOOGLE_APPLICATION_CREDENTIALS: " + e.getMessage());
                    }
                }

                // 2) Fallback to classpath resource for local development
                ClassPathResource resource = new ClassPathResource(CREDENTIALS_FILE);
                if (resource.exists()) {
                    try (InputStream serviceAccount = resource.getInputStream()) {
                        FirebaseOptions options = FirebaseOptions.builder()
                                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                                .build();

                        FirebaseApp.initializeApp(options);
                        System.out.println("Firebase initialized successfully with service account from classpath!");
                    }
                } else {
                    System.err.println("Firebase credentials not provided. Set GOOGLE_APPLICATION_CREDENTIALS or add " + CREDENTIALS_FILE + " to resources.");
                    System.err.println("Continuing without Firebase (using in-memory storage if applicable).");
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