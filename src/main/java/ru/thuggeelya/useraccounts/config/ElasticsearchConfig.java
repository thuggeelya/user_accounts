package ru.thuggeelya.useraccounts.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchClients;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import static org.apache.http.HttpHost.create;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUrl;

    @Bean
    public ElasticsearchClient elasticsearchClient() {

        final RestClientBuilder builder = RestClient.builder(create(elasticsearchUrl));
        return ElasticsearchClients.createImperative(builder.build());
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate(final ElasticsearchClient elasticsearchClient) {
        return new ElasticsearchTemplate(elasticsearchClient);
    }
}
