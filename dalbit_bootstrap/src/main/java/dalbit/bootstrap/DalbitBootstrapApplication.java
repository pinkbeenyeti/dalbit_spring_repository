package dalbit.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "dalbit", exclude = { UserDetailsServiceAutoConfiguration.class })
@ConfigurationPropertiesScan(basePackages = "dalbit")
@EnableScheduling
public class DalbitBootstrapApplication {

    public static void main(String[] args) {
        SpringApplication.run(DalbitBootstrapApplication.class, args);
    }

}
