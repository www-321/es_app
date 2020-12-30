package com.es.demo;

import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.PutIndexTemplateRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootTest
class ApplicationTests {
@Autowired
    RestHighLevelClient restHighLevelClient;

@Autowired
    DataSource dataSource;


    @Test
    void contextLoads() {
    }

    @Test
    public void tt() throws SQLException {


        Connection connection = dataSource.getConnection();
        List<String> list = Arrays.asList("武全", "习近平", "胡锦涛", "温家宝", "obama");

        PreparedStatement ps = connection.prepareStatement("INSERT into user (name,age,createtime) VALUES(?,?,?)");
        for (int i = 0; i <100000; i++) {
            ps.setString(1,list.get(new Random().nextInt(5)));
            ps.setInt(2, new Random().nextInt(i+1));
            ps.setObject(3, new Date());
            ps.addBatch();
        }
        ps.executeBatch();


    }

@Test
    public void testUser() throws IOException {
    CreateIndexRequest createIndexRequest = new CreateIndexRequest("index_bb");

    createIndexRequest.settings(Settings.builder().put("index.number_of_shards", 1).put("index.number_of_replicas", 0));
    XContentBuilder jsonMapping = XContentFactory.jsonBuilder();
    jsonMapping.startObject().startObject("properties")
            .startObject("name")
            .field("type","text")
            .field("analyzer","ik_max_word")
            .field("search_analyzer","ik_smart")
            .startObject("fields")
            .startObject("keyword").field("type","keyword").endObject()
            .endObject()
            .endObject()
            //如果是嵌套文档，一定要在此处声明是nested
            .startObject("human")
            .field("type","nested")
            .startObject("properties")
            .startObject("momname").field("type","keyword").endObject()
            .startObject("dadname").field("type","keyword").endObject()
            .endObject()
            .endObject()
            .startObject("age").field("type","integer").endObject()

            .endObject().endObject();
    createIndexRequest.mapping(jsonMapping);
    CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);


    }



}
