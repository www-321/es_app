package com.es.demo.controller;

import org.apache.lucene.index.Terms;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 聚合查询
 */
@RestController
public class AggController {
    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @GetMapping("agg")
    public Object agg() throws IOException {
        SearchRequest searchRequest = new SearchRequest("index_user_nj");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.aggregation(
                AggregationBuilders.terms("agg_name")
                .field("name.keyword")
                    .subAggregation(AggregationBuilders.avg("age_avg").field("age"))
        );

        searchRequest.source(sourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        Terms aggNameTerm = search.getAggregations().get("agg_name");

        return null;
    }




}
