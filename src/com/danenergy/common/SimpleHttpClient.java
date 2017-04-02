package com.danenergy.common;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Lior Gad on 3/29/2017.
 */
public class SimpleHttpClient {

        private final String USER_AGENT = "Mozilla/5.0";

//        public static void main(String[] args) throws Exception {
//
//            HttpURLConnectionExample http = new HttpURLConnectionExample();
//
//            System.out.println("Testing 1 - Send Http GET request");
//            http.sendGet();
//
//            System.out.println("\nTesting 2 - Send Http POST request");
//            http.sendPost();
//
//        }

        // HTTP GET request
        public String sendGet(String baseUrl,String body) throws Exception {

            //String url = "http://www.google.com/search?q=mkyong";

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(baseUrl);
            request.addHeader("User-Agent", USER_AGENT);
            //request.setParams(new );
            HttpResponse response = client.execute(request);




// add request header



            String charset = java.nio.charset.StandardCharsets.UTF_8.name();
            String param1 = "address";
// ...

//            String query = String.format("param1=%s",
//                    URLEncoder.encode(param1, charset));


//            String url = baseUrl;
//
//            if(null != body) {
//                url +=  "/" + body;
//            }

//            URL obj = new URL(baseUrl + "?" + query);
//            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//
//
//            // optional default is GET
//            con.setRequestMethod("GET");
//
//            //add request header
//            con.setRequestProperty("User-Agent", USER_AGENT);
//            con.setRequestProperty("Accept-Charset", charset);
//
//
//            int responseCode = con.getResponseCode();
//            System.out.println("\nSending 'GET' request to URL : " + obj);
//            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            String inputLine;
            StringBuffer responseStr = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                responseStr.append(inputLine);
            }
            in.close();


            //print result
            System.out.println(response.toString());

            return response.toString();

        }
}
