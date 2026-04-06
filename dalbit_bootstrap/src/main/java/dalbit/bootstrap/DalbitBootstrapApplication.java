package dalbit.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = "dalbit", exclude = { UserDetailsServiceAutoConfiguration.class })
@ConfigurationPropertiesScan(basePackages = "dalbit")
public class DalbitBootstrapApplication {

    public static void main(String[] args) {
        SpringApplication.run(DalbitBootstrapApplication.class, args);
    }

}
