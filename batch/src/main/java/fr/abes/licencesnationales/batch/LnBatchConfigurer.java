package fr.abes.licencesnationales.batch;

import fr.abes.licencesnationales.core.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.MapJobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

@Slf4j
public class LnBatchConfigurer implements BatchConfigurer {
    private final EntityManagerFactory entityManagerFactory;

    private PlatformTransactionManager transactionManager;

    private JobRepository jobRepository;

    private JobLauncher jobLauncher;

    private JobExplorer jobExplorer;

    /**
     * Create a new {@link LnBatchConfigurer} instance.
     * @param entityManagerFactory the entity manager factory
     */
    public LnBatchConfigurer(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public JobRepository getJobRepository() {
        return this.jobRepository;
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    @Override
    public JobLauncher getJobLauncher() {
        return this.jobLauncher;
    }

    @Override
    public JobExplorer getJobExplorer() {
        return this.jobExplorer;
    }

    @PostConstruct
    public void initialize() {
        try {
            // transactionManager:
            log.warn("Forcing the use of a JPA transactionManager");
            if(this.entityManagerFactory == null){
                log.error("Unable to initialize batch configurer : entityManagerFactory must not be null");
            } else {
                this.transactionManager = new JpaTransactionManager(this.entityManagerFactory);
            }

            // jobRepository:
            log.warn("Forcing the use of a Map based JobRepository");
            MapJobRepositoryFactoryBean jobRepositoryFactory = new MapJobRepositoryFactoryBean(this.transactionManager);
            jobRepositoryFactory.afterPropertiesSet();
            this.jobRepository = jobRepositoryFactory.getObject();

            // jobLauncher:
            SimpleJobLauncher jobLauncherParam = new SimpleJobLauncher();
            jobLauncherParam.setJobRepository(getJobRepository());
            jobLauncherParam.afterPropertiesSet();
            this.jobLauncher = jobLauncherParam;

            // jobExplorer:
            MapJobExplorerFactoryBean jobExplorerFactory = new MapJobExplorerFactoryBean(jobRepositoryFactory);
            jobExplorerFactory.afterPropertiesSet();
            this.jobExplorer = jobExplorerFactory.getObject();
        }
        catch (Exception ex) {
            throw new IllegalStateException(Constant.ERROR_SPRING_BATCH, ex);
        }
    }
}
