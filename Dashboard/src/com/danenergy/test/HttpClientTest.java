package com.danenergy.test;

import com.danenergy.Cluster;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.common.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by dev on 29/05/2017.
 */
public class HttpClientTest {

    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static final JsonFactory JSON_FACTORY = new JacksonFactory();

    public static class BmsEngineUrl extends GenericUrl {

        public BmsEngineUrl(String encodedUrl) {
            super(encodedUrl);
        }

        @Key
        public String fields;
    }

    public static void main(String[] args) {
        httpClientTest();
    }

    public static class ClusterJson extends GenericJson {

        /** Cluster object. */
        @Key("cluster")
        private Cluster cluster;

        public Cluster getCluster() {
            return this.cluster;
        }
    }


    private static void httpClientTest()
    {
        try {
            HttpRequestFactory requestFactory =
                    HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                        @Override
                        public void initialize(HttpRequest request) {
                            request.setParser(new JsonObjectParser(JSON_FACTORY));
                        }
                    });
            BmsEngineUrl url = new BmsEngineUrl("http://localhost:8080");
            url.set("name","cluster");
            HttpRequest request = requestFactory.buildGetRequest(url);

            HttpResponse response = request.execute();

            ClusterJson json = response.parseAs(ClusterJson.class);

            Cluster cluster = json.getCluster();

            int i=0;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
