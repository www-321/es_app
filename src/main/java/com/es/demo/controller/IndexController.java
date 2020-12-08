package com.es.demo.controller;

import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.FreezeIndexRequest;
import org.elasticsearch.client.indices.PutIndexTemplateRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
public class IndexController {

@Autowired
    private RestHighLevelClient restHighLevelClient;




    @GetMapping("get_api")
    public void getApi() {
        GetRequest getRequest = new GetRequest("index_user_man", "1");

    }

    public void searchApi() {


        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(new String[]{"index_user_man"});
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.sort("age", SortOrder.DESC);


    }

    /**
     * 创建模版
     * @throws IOException
     */
    @GetMapping("template")
    public void putTemplate() throws IOException {
        PutIndexTemplateRequest templateRequest = new PutIndexTemplateRequest("index_user_template");
        //匹配 index_user 开头的索引，都会使用这个模版创建
        templateRequest.patterns(Arrays.asList("index_user*"));

        templateRequest.settings(Settings.builder()
                //数据插入后多久能查到，实时性要求高可以调低
                .put("index.refresh_interval", "10s")
                //传输日志，对数据安全性要求高的 设置 request，默认值:request
                .put("index.translog.durability", "async")
                .put("index.translog.sync_interval", "120s")
                //分片数量
                .put("index.number_of_shards", "5")
                //副本数量
                .put("index.number_of_replicas", "0")
                //单次最大查询数据的数量。默认10000。不要设置太高，如果有导出需求可以根据查询条件分批次查询。
                .put("index.max_result_window", "100000"));

        XContentBuilder jsonMapping = XContentFactory.jsonBuilder();
        jsonMapping.startObject().startObject("properties")
                .startObject("name").field("type", "text").field("analyzer", "ik_max_word").endObject()
                .startObject("addr").field("type", "keyword").endObject()
                .startObject("ipAddr").field("type", "ip").endObject()
                .startObject("age").field("type", "long").endObject()
                .startObject("createTime").field("type", "date").field("format","yyyy-MM-dd HH:mm:ss").endObject()
                .endObject().endObject();
        templateRequest.mapping(jsonMapping);

        AcknowledgedResponse acknowledgedResponse = restHighLevelClient.indices().putTemplate(templateRequest, RequestOptions.DEFAULT);
        //设置为true只强制创建，而不是更新索引模板。如果它已经存在，它将失败
        templateRequest.create(false);

    }

    @PostMapping("index")
    public void index() throws IOException {
        //创建索引 可以使用模版
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("index_user_nj_2020-01-02");
        restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
    }


    /**
     * 单个索引设置
     * @throws IOException
     */
    @GetMapping("mapping")
    public void mapping() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("index_user_nj");

        createIndexRequest.settings(Settings.builder().put("index.number_of_shards", 1).put("index.number_of_replicas", 0));

        XContentBuilder jsonMapping = XContentFactory.jsonBuilder();
        jsonMapping.startObject().startObject("properties")
                .startObject("name").field("type", "text").field("analyzer", "ik_max_word").endObject()
                .startObject("addr").field("type", "keyword").endObject()
                .startObject("ipAddr").field("type", "ip").endObject()
                .startObject("age").field("type", "long").endObject()
                .startObject("createTime").field("type", "date").field("format","yyyy-MM-dd HH:mm:ss").endObject()
                .endObject().endObject();
        createIndexRequest.mapping(jsonMapping);
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);

    }

    @GetMapping("insert")
    public void insert() throws IOException {

        IndexRequest indexRequest = new IndexRequest("index_user_nj*");
        JSONObject obj = new JSONObject();
        obj.put("name", "武全");
        obj.put("age", 18);
        obj.put("ip", "123.36.59.2");
        obj.put("addr", "中国");
        obj.put("createTime", System.currentTimeMillis());
        indexRequest.source(obj);
        IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        index.status().getStatus();


    }

    @GetMapping("getById")
    public void getById(String id) throws IOException {
        GetRequest getRequest = new GetRequest("index_user_nj*", id);

        GetResponse documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);

        String source = documentFields.getSourceAsString();

    }

    /**
     *
     * @param id docId
     */
    @GetMapping("del_by_id")
    public void delById(String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.id(id);
        deleteRequest.index("index_user_nj");
        DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);

    }

    /**
     * 删除索引
     * @throws IOException
     */
    @GetMapping("del_index")
    public void delIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("index_user_nj*");
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);

    }


    /**
     * 批量插入
     */
    @GetMapping("bulk_insert")
    public void bulk() throws IOException {

        BulkRequest bulkRequest = new BulkRequest("index_user_nj");
        for (int i = 0; i < 10000; i++) {
            JSONObject obj = new JSONObject();
            obj.put("age", i + 11);
            obj.put("name", "武全" + i);
            obj.put("addr", "中国");
            obj.put("ip", "12.1.0." + new Random().nextInt(255));
            bulkRequest.add(new IndexRequest().source(obj));
            //批量删除
            //bulkRequest.add(new DeleteRequest("index_user_nj", i + ""));
            //批量更新
            //bulkRequest.add(new UpdateRequest("index", "id"));
        }

        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }


    @GetMapping("update")
    public void update() throws IOException {
        //根据文档id更新
        UpdateRequest updateRequest = new UpdateRequest("index_user_nj", "id1");
        Map<String, Object> map = new HashMap<>();
        map.put("name", "彭德怀");
        updateRequest.doc(map);
        restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

        //不知道id
        UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest("index_user_nj*");
        updateByQueryRequest.setQuery(QueryBuilders.termQuery("name.keyword", "武全"));
        updateByQueryRequest.setScript(new Script(
                Script.DEFAULT_SCRIPT_TYPE, "painless", "if (ctx._source.name= '武全') {ctx._source.name ='武全2'} ", Collections.emptyMap()
        ));
        restHighLevelClient.updateByQuery(updateByQueryRequest, RequestOptions.DEFAULT);

    }


    @GetMapping("search")
    public void search() throws IOException {
        SearchRequest searchRequest = new SearchRequest("index_user_nj*");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //模糊查询
        searchSourceBuilder.query(QueryBuilders.wildcardQuery("name"+".keyword", "*" +"张三" + "*"));

        searchSourceBuilder.query(QueryBuilders.termQuery("name.keyword", "武全"));
        searchSourceBuilder.sort("age", SortOrder.DESC);
        searchSourceBuilder.from(1);
        searchSourceBuilder.size(10);
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : search.getHits()) {
            String source = hit.getSourceAsString();
        }


    }



    @GetMapping("search2")
    public void search2() throws IOException {
        QueryBuilders.termQuery("name.keyword", "武全");
        QueryBuilders.termsQuery("age", 1, 2, 15);
        QueryBuilders.matchQuery("name", "www");
        QueryBuilders.multiMatchQuery("武全", "name", "age", "addr");
        QueryBuilders.matchAllQuery();
    }




    @GetMapping("search_bool")
    public void searchBool() throws IOException {
        QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("name", ""))
                .mustNot(QueryBuilders.termQuery("name", "ww"))
        ;
    }

    @GetMapping("search_ids")
    public void searchIds() throws IOException {
        IdsQueryBuilder idsQueryBuilder = QueryBuilders.idsQuery().addIds("dsdsd", "id2");
    }





























}
