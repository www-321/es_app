package com.es.demo.controller;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
public class IndexController {

@Autowired
    private RestHighLevelClient restHighLevelClient;

    @PostMapping("index")
    public void index(String indexName) throws IOException {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "wuquan");
        jsonMap.put("birthDay", "1994-12-31");
        jsonMap.put("gender", "1");
        IndexRequest request = new IndexRequest(indexName).source(jsonMap);
        request.timeout(TimeValue.timeValueSeconds(1));

        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
    }


    @GetMapping("get_api")
    public void getApi() {
        GetRequest getRequest = new GetRequest("index_user_man", "1");

    }

    public void searchApi() {

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(new String[]{"index_user_man"});
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

    }







}
