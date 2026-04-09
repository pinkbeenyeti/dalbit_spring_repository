package dalbit.adapter.messaging.push.fcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class FcmConfig {

    @Value("${firebase.account-path}")
    private String accountPath;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        InputStream serviceAccount = new PathResource(accountPath).getInputStream();

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();

        FirebaseApp app = FirebaseApp.getApps().isEmpty()
            ? FirebaseApp.initializeApp(options)
            : FirebaseApp.getInstance();

        return FirebaseMessaging.getInstance(app);
    }

    @Bean(name = "fcmExecutor")
    public Executor fcmExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("FCM-Callback-");
        executor.initialize();

        return executor;
    }
}
