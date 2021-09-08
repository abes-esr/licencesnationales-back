package fr.abes.licencesnationales.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"fr.abes.licencesnationales.batch","fr.abes.licencesnationales.core"})
@EntityScan("fr.abes.licencesnationales.core")
@EnableJpaRepositories(basePackages = "fr.abes.licencesnationales.core.repository")
public class BatchApplication {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        SpringApplication.run(BatchApplication.class, args);
        long endTime = System.currentTimeMillis();
        log.debug("Timing " + (endTime-startTime) +" ms");
        System.exit(0);
    }
}
