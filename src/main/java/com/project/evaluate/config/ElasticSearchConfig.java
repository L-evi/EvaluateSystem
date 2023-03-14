package com.project.evaluate.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {
    private String scheme;

    private String address;

    private Integer connectTimeout;

    private Integer socketTimeout;

    private Integer tryConnectTimeout;

    private Integer maxConnectCount;

    private Integer maxConnectPerRoute;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
    }
//    参考链接：https://blog.csdn.net/u012674854/article/details/106544493?spm=1001.2101.3001.6661.1&utm_medium=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-106544493-blog-105932047.pc_relevant_multi_platform_whitelistv4&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-106544493-blog-105932047.pc_relevant_multi_platform_whitelistv4&utm_relevant_index=1
//    https://blog.csdn.net/weixin_39025362/article/details/105932047
//    https://blog.csdn.net/weixin_39025362/article/details/105360676
}
