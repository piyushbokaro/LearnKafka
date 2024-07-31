package com.kafka.demo.opensearch;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class OpenSearchConsumer {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(OpenSearchConsumer.class.getSimpleName());
        // Create an OpenSearch Client
        RestHighLevelClient highLevelClient = createOpenSearchClient();
        // Create a Kafka client

        //
    }

    public static RestHighLevelClient createOpenSearchClient () {
        String connectionString = "http://localhost:9200";

        RestHighLevelClient restHighLevelClient;

        URI connectUri = URI.create(connectionString);
        String userInfo = connectUri.getUserInfo();

        if (userInfo == null) {
            restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost(connectUri.getHost()
                    , connectUri.getPort(), connectUri.getScheme())));
        } else {
            String[] auth = userInfo.split(":");
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(auth[0], auth[1]));

            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(new HttpHost(connectUri.getHost(), connectUri.getPort(), connectUri.getScheme()))
                            .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                                    .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())));

        }
        return restHighLevelClient;
    }
}
