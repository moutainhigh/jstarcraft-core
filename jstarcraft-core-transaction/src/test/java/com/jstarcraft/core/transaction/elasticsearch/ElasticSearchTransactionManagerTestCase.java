package com.jstarcraft.core.transaction.elasticsearch;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.jstarcraft.core.transaction.TransactionManager;
import com.jstarcraft.core.transaction.TransactionManagerTestCase;

import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.IndexSettings;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

public class ElasticSearchTransactionManagerTestCase extends TransactionManagerTestCase {

    private static final int EMBEDDED_ELASTIC_PORT = 9350;

    private static EmbeddedElastic embeddedElastic;

    private RestHighLevelClient highLevelClient;

    @Before
    public void testBefore() {
        highLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", EMBEDDED_ELASTIC_PORT, "http")));
    }

    @After
    public void testAfter() throws IOException {
        highLevelClient.close();
    }

    @BeforeClass
    public static void startEmbeddedElastic() throws IOException, InterruptedException {
        embeddedElastic = EmbeddedElastic.builder().withElasticVersion("6.4.0")

                .withSetting(PopularProperties.HTTP_PORT, EMBEDDED_ELASTIC_PORT)

                .withStartTimeout(2, TimeUnit.MINUTES)

                .withIndex(ElasticSearchTransactionManager.DEFAULT_INDEX, IndexSettings.builder()

                        .withType(ElasticSearchTransactionManager.DEFAULT_TYPE, ClassLoader.getSystemResourceAsStream("ElasticSearchTransactionDefinition.mapping.json"))

                        .build())

                .build()

                .start();
    }

    @AfterClass
    public static void stopEmbeddedElastic() {
        if (embeddedElastic != null) {
            embeddedElastic.stop();
        }
    }

    @Override
    protected TransactionManager getDistributionManager() {
        return new ElasticSearchTransactionManager(highLevelClient);
    }

}