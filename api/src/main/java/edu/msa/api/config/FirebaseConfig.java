package edu.msa.api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

@Component
public class FirebaseConfig {

    @Bean
    public void setupFirebase() throws IOException {
        //TODO: add the file in resources and copy the local path here

        FileInputStream serviceAccount =
                new FileInputStream("C:\\Users\\eliza\\StudioProjects\\In-Touch\\api\\src\\main\\resources\\intouch-c623b-firebase-adminsdk-1y17n-cbd7da8104.json");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
    }
}
