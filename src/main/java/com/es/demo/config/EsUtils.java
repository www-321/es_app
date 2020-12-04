package com.es.demo.config;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
@Component
public class EsUtils {



    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient restClient;

    public IndexResponse createIndex(String indexName) throws IOException {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "wuquan");
        jsonMap.put("birthDay", "1994-12-31");
        jsonMap.put("gender", "1");
        IndexRequest request = new IndexRequest(indexName).source(jsonMap);
        request.timeout(TimeValue.timeValueSeconds(1));

        return restClient.index(request, RequestOptions.DEFAULT);
    }



}
