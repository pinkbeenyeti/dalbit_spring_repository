package dalbit.adapter.persistence.jpa.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing // 생성일/수정일 자동화를 위해 필수
@EnableJpaRepositories(
    basePackages = "dalbit.adapter.persistence.jpa",
    repositoryImplementationPostfix = "Impl"
)
@EntityScan(basePackages = "dalbit.adapter.persistence.jpa")
public class JpaConfig {

}
