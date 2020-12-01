package com.es.demo.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsHighRestClientConfig {


    @Bean("restHighLevelClient")
    public static RestHighLevelClient restHighLevelClient() {
        HttpHost httpHost = new HttpHost("localhost", 9200, "http");

        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(httpHost));
        return restHighLevelClient;
    }
}
