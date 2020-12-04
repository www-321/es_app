package com.es.demo;

import com.es.demo.config.EsUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.lang.model.util.ElementScanner6;
import java.io.IOException;

@SpringBootTest
class ApplicationTests {
    @Autowired
    EsUtils EsUtils;
    @Test
    void contextLoads() {
    }

    @Test
    public void test3() throws IOException {
        System.out.println(EsUtils.createIndex("index_user_woman"));
    }

}
