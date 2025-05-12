package ru.thuggeelya.useraccounts.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;
import static org.springframework.transaction.TransactionDefinition.ISOLATION_READ_COMMITTED;
import static org.springframework.transaction.TransactionDefinition.ISOLATION_SERIALIZABLE;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;

@EnableAsync
@Configuration
@EnableScheduling
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class ApplicationConfig {

    @Bean
    public ObjectMapper objectMapper() {

        final ObjectMapper mapper = new ObjectMapper();

        mapper.setSerializationInclusion(NON_NULL);
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        return mapper;
    }

    @Bean
    public TransactionTemplate transactionTemplateSerializable(final PlatformTransactionManager transactionManager) {

        final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        transactionTemplate.setIsolationLevel(ISOLATION_SERIALIZABLE);
        transactionTemplate.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);

        return transactionTemplate;
    }

    @Bean
    public TransactionTemplate transactionTemplate(final PlatformTransactionManager transactionManager) {

        final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        transactionTemplate.setIsolationLevel(ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);

        return transactionTemplate;
    }
}
