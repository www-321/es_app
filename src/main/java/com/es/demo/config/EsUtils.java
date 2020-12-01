package com.es.demo.config;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Map;

public class EsUtils {
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient restClient;

    public void createIndex(String indexName) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "wuquan");
        jsonMap.put("birthDay", "1994-12-31");
        jsonMap.put("gender", "1");
        IndexRequest request = new IndexRequest("index_user").source(jsonMap);
//        restHighLevelClient.
    }
}
